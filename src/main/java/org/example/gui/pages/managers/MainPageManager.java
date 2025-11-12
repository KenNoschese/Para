package org.example.gui.pages.managers;

import org.example.DatabaseManager.RouteDatabase.*;
import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.appManager.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

import static org.example.gui.resources.Fonts.*;

public class MainPageManager {

    @SuppressWarnings("unused")
    private static double getDoubleSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        if (obj == null) return 0.0;
        return ((Number) obj).doubleValue();
    }

    @SuppressWarnings("unused")
    private static int getIntSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        if (obj == null) return 0;
        return ((Number) obj).intValue();
    }

    private String currentFilter = "all";
    private RouteManager routeManager;

    public MainPageManager() {
        this.routeManager = new RouteManager();
    }

    public RouteManager getRouteManager() {
        return routeManager;
    }

    public int getCurrentUserId() {
        String query = "SELECT user_id FROM ActiveSession LIMIT 1";
        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println("[DEBUG] ActiveSession user_id = " + userId); // ← ADD THIS
                return userId;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] getCurrentUserId failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[DEBUG] No ActiveSession found");
        return -1;
    }

    // ────────────────────────────────────────────────────────────
    // BUSINESS LOGIC: TAKE ROUTE
    // ────────────────────────────────────────────────────────────
    public TakeRouteResult takeRoute(RouteComponent route) {
        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            List<RouteComponent> segments = getSegments(route);

            // For transfer routes, we need to board all segments
            List<RouteManager.JeepneyInfo> jeepneyInfos = new ArrayList<>();

            for (RouteComponent segment : segments) {
                ArrayList<RouteManager.JeepneyInfo> jeepneys =
                        routeManager.getJeepneysForRoute(segment.getRoute());

                if (jeepneys.isEmpty()) {
                    return TakeRouteResult.error(
                            "No jeepneys available on route: " + segment.getRoute()
                    );
                }

                RouteManager.JeepneyInfo available = jeepneys.stream()
                        .filter(j -> !j.isFull())
                        .findFirst()
                        .orElse(null);

                if (available == null) {
                    return TakeRouteResult.error(
                            "All jeepneys are full on route: " + segment.getRoute()
                    );
                }

                jeepneyInfos.add(available);
            }

            // Board all jeepneys for all segments
            for (RouteManager.JeepneyInfo jeepney : jeepneyInfos) {
                routeManager.boardJeepney(jeepney.getPlateNumber());
            }

            // Return the first jeepney info (for display purposes)
            return TakeRouteResult.success(jeepneyInfos.get(0), segments);

        } catch (Exception ex) {
            return TakeRouteResult.error("Error boarding jeepney: " + ex.getMessage());
        }
    }

    public CompleteRouteResult completeRoute(StateManager.ActiveTrip trip) {
        if (trip == null) {
            return CompleteRouteResult.error("No active trip");
        }

        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            // Get all segments
            List<RouteComponent> segments = getSegments(trip.getRoute());

            // Leave all jeepneys for all segments
            for (RouteComponent segment : segments) {
                ArrayList<RouteManager.JeepneyInfo> jeepneys =
                        routeManager.getJeepneysForRoute(segment.getRoute());

                for (RouteManager.JeepneyInfo jeepney : jeepneys) {
                    // Leave the jeepney if it was the one we boarded
                    routeManager.leaveJeepney(jeepney.getPlateNumber());
                }
            }

            return CompleteRouteResult.success(trip);

        } catch (Exception ex) {
            return CompleteRouteResult.error("Error completing trip: " + ex.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────
    // SEARCH ROUTES
    // ────────────────────────────────────────────────────────────
    public SearchResult searchRoutes(String from, String to) throws SQLException {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return SearchResult.empty("Please enter both current location and destination.");
        }

        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            if ("all".equals(currentFilter)) {
                return searchAllRoutes(from, to, conn);
            } else {
                return searchFilteredRoutes(from, to, conn);
            }
        }
    }

    private SearchResult searchAllRoutes(String from, String to, Connection conn) throws SQLException {
        ArrayList<RouteComponent> allPossible = routeManager.findRoutesWithTransfers(from, to);
        allPossible = removeDuplicateRouteOptions(allPossible);

        if (allPossible.isEmpty()) {
            return SearchResult.empty("No routes found from " + from + " to " + to);
        }

        RouteComponent firstRoute = allPossible.get(0);
        ArrayList<RouteComponent> defaultSegments = getSegments(firstRoute);
        return new SearchResult(allPossible, defaultSegments, true);
    }

    private SearchResult searchFilteredRoutes(String from, String to, Connection conn) throws SQLException {
        NavigationFacade navigationFacade = new NavigationFacade();
        ArrayList<RouteComponent> bestFullRoute = navigationFacade.findBestRoute(from, to, currentFilter);

        if (bestFullRoute == null || bestFullRoute.isEmpty()) {
            return SearchResult.empty("No routes found for the selected filter.");
        }

        ArrayList<RouteComponent> single = new ArrayList<>();
        single.add(wrapInComposite(bestFullRoute));
        return new SearchResult(single, bestFullRoute, true);
    }

    // ────────────────────────────────────────────────────────────
    // SAVED ROUTES
    // ────────────────────────────────────────────────────────────
    public boolean addSavedRoute(RouteComponent route) {
        int userId = getCurrentUserId();
        System.out.println("[DEBUG] addSavedRoute() → user_id = " + userId);

        if (userId == -1) {
            JOptionPane.showMessageDialog(null, "Please log in to save routes.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String serialized = serializeRoute(route);
        if (serialized == null || serialized.isEmpty()) {
            return false;
        }

        String sql = "INSERT IGNORE INTO Saved_Routes (user_id, route_data) VALUES (?, ?)";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, serialized);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] Save failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving route: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void removeSavedRoute(RouteComponent route) {
        int userId = getCurrentUserId();
        if (userId == -1) {
            System.out.println("[WARN] No active session. Cannot delete route.");
            return;
        }

        String serialized = serializeRoute(route);
        if (serialized == null || serialized.isEmpty()) {
            System.out.println("[ERROR] Failed to serialize route for deletion");
            return;
        }

        String sql = "DELETE FROM Saved_Routes WHERE user_id = ? AND route_data = ?";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, serialized);
            int rows = ps.executeUpdate();
            System.out.println("[SUCCESS] Route deleted. Rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to delete route: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<RouteComponent> getSavedRoutes() {
        ArrayList<RouteComponent> routes = new ArrayList<>();
        int userId = getCurrentUserId();

        System.out.println("[DEBUG] Loading saved routes for user_id = " + userId);

        if (userId == -1) {
            System.out.println("[WARN] No active user. Returning empty saved routes.");
            return routes;
        }

        String sql = "SELECT route_data FROM Saved_Routes WHERE user_id = ? ORDER BY saved_at DESC";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteComponent route = deserializeRoute(rs.getString("route_data"));
                    if (route != null) {
                        routes.add(route);
                        System.out.println("[DEBUG] Loaded route: " + route.getRoute());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to load saved routes: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[DEBUG] Total saved routes loaded: " + routes.size());
        return routes;
    }

    public static class SavedRouteEntry {
        public final String routeData;     // Exact DB string
        public final RouteComponent route; // Deserialized object
        public SavedRouteEntry(String routeData, RouteComponent route) {
            this.routeData = routeData;
            this.route = route;
        }
    }

    public ArrayList<SavedRouteEntry> getSavedRoutesWithData() {
        int userId = getCurrentUserId();
        if (userId == -1) return new ArrayList<>();

        String sql = "SELECT route_data FROM Saved_Routes WHERE user_id = ?";
        ArrayList<SavedRouteEntry> list = new ArrayList<>();

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String routeData = rs.getString("route_data");
                    RouteComponent route = deserializeRoute(routeData);
                    if (route != null) {
                        list.add(new SavedRouteEntry(routeData, route));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[LOAD] Total saved routes loaded: " + list.size());
        return list;
    }

    public void removeSavedRouteByExactData(String routeData) {
        int userId = getCurrentUserId();
        if (userId == -1 || routeData == null) return;

        String sql = "DELETE FROM Saved_Routes WHERE user_id = ? AND route_data = ?";
        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, routeData);
            int rows = ps.executeUpdate();
            System.out.println("[DELETE] Rows affected: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // SERIALIZATION (ROBUST & SAFE)
    // ──────────────────────────────────────────────────────────────
    public String serializeRoute(RouteComponent route) {
        try {
            List<RouteComponent> segments = getSegments(route);
            StringBuilder sb = new StringBuilder();
            sb.append(route.getFromLocation()).append("→")
                    .append(route.getDestination()).append("→")
                    .append(route.getRoute()).append("→")
                    .append(route.getFare()).append("→")
                    .append(route.getEta()).append("→")
                    .append(route.getStops()).append("→")
                    .append(route.getTransfers()).append("→")
                    .append(route.getDetails());

            for (RouteComponent seg : segments) {
                sb.append("|")
                        .append(seg.getFromLocation()).append("^")
                        .append(seg.getDestination()).append("^")
                        .append(seg.getRoute()).append("^")
                        .append(seg.getDetails()).append("^")
                        .append(String.join(",", seg.getRouteStops())).append("^")
                        .append(seg.getStops()).append("^")
                        .append(seg.getEta()).append("^")
                        .append((int)seg.getDistance()).append("^")
                        .append(seg.getFare());
            }

            String result = sb.toString();
            System.out.println("[SERIALIZE] Route data: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private RouteComponent deserializeRoute(String data) {
        if (data == null || data.isEmpty()) return null;

        try {
            String[] parts = data.split("\\|", -1);
            String[] header = parts[0].split("→", -1);

            if (header.length < 8) return null;

            String from = header[0];
            String to = header[1];
            String routeName = header[2];
            double fare = Double.parseDouble(header[3]);
            int eta = Integer.parseInt(header[4]);
            int stops = Integer.parseInt(header[5]);
            int transfers = Integer.parseInt(header[6]);
            String details = header[7];

            List<String> routeStops = List.of();
            if (parts.length > 1) {
                String[] firstSeg = parts[1].split("\\^", -1);
                if (firstSeg.length >= 5 && !firstSeg[4].isEmpty()) {
                    routeStops = Arrays.asList(firstSeg[4].split(","));
                }
            }

            Segments firstSeg = new Segments(
                    from, to, routeName, details,
                    routeStops, stops, eta, 0, fare
            );

            if (transfers == 0) {
                return firstSeg;
            }

            Routes composite = new Routes(from + " to " + to);
            composite.addSegment(firstSeg);

            for (int i = 1; i < parts.length; i++) {
                String[] s = parts[i].split("\\^", -1);
                if (s.length < 9) continue;

                List<String> segStops = s[4].isEmpty() ? List.of() : Arrays.asList(s[4].split(","));

                Segments seg = new Segments(
                        s[0], s[1], s[2], s[3],
                        segStops,
                        Integer.parseInt(s[5]),
                        Integer.parseInt(s[6]),
                        Integer.parseInt(s[7]),
                        Double.parseDouble(s[8])
                );
                composite.addSegment(seg);
            }
            return composite;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ────────────────────────────────────────────────────────────
    // FILTER & STRATEGY
    // ────────────────────────────────────────────────────────────
    public void setFilter(String filter) {
        this.currentFilter = filter != null ? filter : "all";
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public RouteDetails calculateTransferMetrics(RouteComponent route) {
        if (route == null) return new RouteDetails(0, 0, 0.0, 0);
        return new RouteDetails(route.getEta(), route.getStops(), route.getFare(), route.getTransfers());
    }

    // ────────────────────────────────────────────────────────────
    // UTILITIES
    // ────────────────────────────────────────────────────────────
    private ArrayList<RouteComponent> removeDuplicateRouteOptions(ArrayList<RouteComponent> list) {
        Set<String> seen = new HashSet<>();
        ArrayList<RouteComponent> uniq = new ArrayList<>();
        for (RouteComponent route : list) {
            String key = route.getFromLocation() + "→" + route.getDestination() +
                    "→" + route.getRoute() + "→" + route.getTransfers();
            if (seen.add(key)) {
                uniq.add(route);
            }
        }
        return uniq;
    }

    public List<String> getAllStopNames() throws SQLException {
        List<String> stops = new ArrayList<>();
        String query = "SELECT stop_name FROM Stops ORDER BY stop_name";
        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                stops.add(rs.getString("stop_name"));
            }
        }
        return stops;
    }

    public boolean validateInput(String from, String to) {
        return from != null && !from.trim().isEmpty() &&
                to != null && !to.trim().isEmpty();
    }

    // ────────────────────────────────────────────────────────────
    // UI: LOCATION TABLE
    // ────────────────────────────────────────────────────────────
    public JTable createLocationTable(BiConsumer<String, Boolean> onLocationSelected,
                                      boolean isFrom) throws SQLException, IOException, FontFormatException {

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Location"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(loadCustomFont(DM_SANS_REGULAR, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setTableHeader(null);

        // Apply initial theme
        ThemeManager theme = ThemeManager.getInstance();
        updateTableTheme(table, theme);

        // Custom renderer that respects theme
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ThemeManager currentTheme = ThemeManager.getInstance();

                if (c instanceof JLabel label) {
                    label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    if (!isSelected) {
                        label.setBackground(currentTheme.getWhite());
                        label.setForeground(currentTheme.getBlack());
                        try { label.setFont(loadCustomFont(DM_SANS_REGULAR, 13)); }
                        catch (Exception ignored) {}
                    } else {
                        label.setBackground(currentTheme.getYellow());
                        label.setForeground(currentTheme.getBlack());
                        try { label.setFont(loadCustomFont(DM_SANS_BOLD, 13)); }
                        catch (Exception ignored) {}
                    }
                }
                return c;
            }
        });

        List<String> stopNames = getAllStopNames();
        for (String stop : stopNames) {
            model.addRow(new Object[]{stop});
        }

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String selected = (String) model.getValueAt(row, 0);
                    onLocationSelected.accept(selected, isFrom);
                }
            }

            @Override public void mouseEntered(MouseEvent e) {
                table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override public void mouseExited(MouseEvent e) {
                table.setCursor(Cursor.getDefaultCursor());
            }
        });

        table.addMouseMotionListener(new MouseMotionAdapter() {
            private int lastRow = -1;
            @Override public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    table.repaint();
                }
            }
        });

        return table;
    }

    private void updateTableTheme(JTable table, ThemeManager theme) {
        table.setBackground(theme.getWhite());
        table.setForeground(theme.getBlack());
        table.setSelectionBackground(theme.getYellow());
        table.setSelectionForeground(theme.getBlack());
        table.setGridColor(theme.getWhite());
    }


    public JScrollPane createTableScrollPane(JTable table, ThemeManager themeManager) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getGray(), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(themeManager.getWhite());
        scrollPane.setBackground(themeManager.getWhite());

        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setPreferredSize(new Dimension(8, 0));
        vBar.setBackground(themeManager.getBackgroundColor());
        vBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = themeManager.getBlue().brighter();
                this.trackColor = themeManager.getBackgroundColor();
            }

            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });

        return scrollPane;
    }

    // ────────────────────────────────────────────────────────────
    // HELPERS
    // ────────────────────────────────────────────────────────────
    public ArrayList<RouteComponent> getSegments(RouteComponent route) {
        if (route instanceof Routes composite) {
            return new ArrayList<>(composite.getSegments());
        } else {
            return new ArrayList<>(List.of(route));
        }
    }

    private RouteComponent wrapInComposite(List<RouteComponent> segments) {
        if (segments.isEmpty()) return null;
        Routes composite = new Routes(segments.get(0).getFromLocation() + " to " + segments.get(segments.size()-1).getDestination());
        segments.forEach(composite::addSegment);
        return composite;
    }

    // ────────────────────────────────────────────────────────────
    // INNER CLASSES
    // ────────────────────────────────────────────────────────────
    public static class SearchResult {
        private final ArrayList<RouteComponent> allRoutes;
        private final ArrayList<RouteComponent> defaultRoute;
        private final boolean success;
        private final String message;

        public SearchResult(ArrayList<RouteComponent> allRoutes,
                            ArrayList<RouteComponent> defaultRoute,
                            boolean success) {
            this.allRoutes = allRoutes != null ? allRoutes : new ArrayList<>();
            this.defaultRoute = defaultRoute != null ? defaultRoute : new ArrayList<>();
            this.success = success;
            this.message = "";
        }

        private SearchResult(String message) {
            this.allRoutes = new ArrayList<>();
            this.defaultRoute = new ArrayList<>();
            this.success = false;
            this.message = message != null ? message : "";
        }

        public static SearchResult empty(String message) { return new SearchResult(message); }
        public ArrayList<RouteComponent> getAllRoutes() { return allRoutes; }
        public ArrayList<RouteComponent> getDefaultRoute() { return defaultRoute; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public boolean isTransferRoute() { return defaultRoute.size() > 1; }
        public boolean isDirect() { return defaultRoute.size() == 1; }
    }

    public static class RouteDetails {
        private final int totalETA;
        private final int totalStops;
        private final double totalFare;
        private final int transfers;

        public RouteDetails(int totalETA, int totalStops, double totalFare, int transfers) {
            this.totalETA = totalETA;
            this.totalStops = totalStops;
            this.totalFare = totalFare;
            this.transfers = transfers;
        }

        public int getTotalETA() { return totalETA; }
        public int getTotalStops() { return totalStops; }
        public double getTotalFare() { return totalFare; }
        public int getTransfers() { return transfers; }
        public int getSegments() { return transfers + 1; }
    }

    public static class TakeRouteResult {
        private final boolean success;
        private final String errorMessage;
        private final RouteManager.JeepneyInfo jeepney;
        private final List<RouteComponent> segments;

        private TakeRouteResult(boolean success, String errorMessage,
                                RouteManager.JeepneyInfo jeepney,
                                List<RouteComponent> segments) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.jeepney = jeepney;
            this.segments = segments;
        }

        public static TakeRouteResult success(RouteManager.JeepneyInfo jeepney,
                                              List<RouteComponent> segments) {
            return new TakeRouteResult(true, null, jeepney, segments);
        }

        public static TakeRouteResult error(String message) {
            return new TakeRouteResult(false, message, null, null);
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public RouteManager.JeepneyInfo getJeepney() { return jeepney; }
        public List<RouteComponent> getSegments() { return segments; }
    }

    public static class CompleteRouteResult {
        private final boolean success;
        private final String errorMessage;
        private final StateManager.ActiveTrip trip;

        private CompleteRouteResult(boolean success, String errorMessage,
                                    StateManager.ActiveTrip trip) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.trip = trip;
        }

        public static CompleteRouteResult success(StateManager.ActiveTrip trip) {
            return new CompleteRouteResult(true, null, trip);
        }

        public static CompleteRouteResult error(String message) {
            return new CompleteRouteResult(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public StateManager.ActiveTrip getTrip() { return trip; }
    }
}