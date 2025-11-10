package org.example.gui.pages.managers;

import org.example.DatabaseManager.RouteDatabase.NavigationFacade;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.Routes;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.resources.Fonts;

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

    // ──────────────────────────────────────────────────────────────
    // SAFE DB READERS
    // ──────────────────────────────────────────────────────────────
    private static double getDoubleSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        if (obj == null) return 0.0;
        return ((Number) obj).doubleValue();   // works for Integer, Long, Double, BigDecimal
    }

    private static int getIntSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        if (obj == null) return 0;
        return ((Number) obj).intValue();
    }

    private final ArrayList<RouteComponent> savedRoutes;
    private String currentFilter = "all";

    public MainPageManager() {
        this.savedRoutes = new ArrayList<>();
    }

    public SearchResult searchRoutes(String from, String to) throws SQLException {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return SearchResult.empty("Please enter both current location and destination.");
        }

        String category = getUserCategory();
        Connection conn = DatabaseInstance.getInstance().getConnection();

        if (currentFilter.equals("all")) {
            return searchAllRoutes(from, to, category, conn);
        } else {
            return searchFilteredRoutes(from, to, category, conn);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // FIXED: searchAllRoutes — now uses RouteManager with safe DB reads
    // ──────────────────────────────────────────────────────────────
    private SearchResult searchAllRoutes(String from, String to, String category, Connection conn) throws SQLException {
        RouteManager routeManager = new RouteManager();
        ArrayList<RouteComponent> allPossible = routeManager.findRoutesWithTransfers(from, to, category, conn);
        allPossible = removeDuplicateRouteOptions(allPossible);

        if (allPossible.isEmpty()) {
            return SearchResult.empty("No routes found from " + from + " to " + to);
        }

        RouteComponent firstRoute = allPossible.get(0);
        ArrayList<RouteComponent> defaultSegments = getSegments(firstRoute);
        return new SearchResult(allPossible, defaultSegments, true);
    }

    // ──────────────────────────────────────────────────────────────
    // FIXED: searchFilteredRoutes — now uses safe DB reads inside NavigationFacade
    // ──────────────────────────────────────────────────────────────
    private SearchResult searchFilteredRoutes(String from, String to, String category, Connection conn) throws SQLException {
        NavigationFacade navigationFacade = new NavigationFacade();
        ArrayList<RouteComponent> bestFullRoute = navigationFacade.findBestRoute(from, to, category, currentFilter, conn);

        if (bestFullRoute == null || bestFullRoute.isEmpty()) {
            return SearchResult.empty("No routes found for the selected filter.");
        }

        ArrayList<RouteComponent> single = new ArrayList<>();
        single.add(wrapInComposite(bestFullRoute));
        return new SearchResult(single, bestFullRoute, true);
    }

    private String getUserCategory() {
        String category = "Student";
        String query = "SELECT password FROM ActiveSession LIMIT 1";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String pswd = rs.getString("password");
                if (pswd != null && !pswd.isEmpty()) {
                    char first = pswd.charAt(0);
                    category = switch (first) {
                        case '1' -> "Regular";
                        case '2' -> "Student";
                        case '3' -> "PWD";
                        case '4' -> "Senior Citizen";
                        default -> "Student";
                    };
                }
            }
        } catch (Exception ignored) {
            // Fallback
        }
        return category;
    }

    public boolean addSavedRoute(RouteComponent route) {
        if (route == null) return false;

        for (RouteComponent existing : savedRoutes) {
            if (isSameRoute(existing, route)) {
                return false;
            }
        }

        savedRoutes.add(route);
        return true;
    }

    public void removeSavedRoute(RouteComponent route) {
        savedRoutes.remove(route);
    }

    public ArrayList<RouteComponent> getSavedRoutes() {
        return new ArrayList<>(savedRoutes);
    }

    public void clearSavedRoutes() {
        savedRoutes.clear();
    }

    private boolean isSameRoute(RouteComponent r1, RouteComponent r2) {
        List<RouteComponent> segs1 = getSegments(r1);
        List<RouteComponent> segs2 = getSegments(r2);
        if (segs1.size() != segs2.size()) return false;

        for (int i = 0; i < segs1.size(); i++) {
            RouteComponent s1 = segs1.get(i);
            RouteComponent s2 = segs2.get(i);
            if (!s1.getRoute().equals(s2.getRoute()) ||
                    !s1.getFromLocation().equals(s2.getFromLocation()) ||
                    !s1.getDestination().equals(s2.getDestination())) {
                return false;
            }
        }
        return true;
    }

    public void setFilter(String filter) {
        this.currentFilter = filter;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public RouteDetails calculateTransferMetrics(RouteComponent route) {
        if (route == null) {
            return new RouteDetails(0, 0, 0.0, 0);
        }

        int transfers = route.getTransfers();
        return new RouteDetails(route.getEta(), route.getStops(), route.getFare(), transfers);
    }

    public RouteStrategy getStrategyForPriority(String priority) {
        return switch (priority.toLowerCase()) {
            case "distance" -> new ShortestDistanceStrategy();
            case "time", "eta" -> new ShortestTimeStrategy();
            case "transfers", "stops" -> new LeastTransferStrategy();
            case "fare" -> new CheapestFareStrategy();
            default -> new ShortestTimeStrategy();
        };
    }

    private ArrayList<RouteComponent> removeDuplicateRouteOptions(ArrayList<RouteComponent> list) {
        Set<String> seen = new HashSet<>();
        ArrayList<RouteComponent> uniq = new ArrayList<>();

        for (RouteComponent route : list) {
            String key = route.getFromLocation() + "→" + route.getDestination() + "→" +
                    route.getRoute() + "→" + route.getTransfers();
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

    // === Helper: Extract segments from RouteComponent ===
    private ArrayList<RouteComponent> getSegments(RouteComponent route) {
        if (route instanceof Routes composite) {
            return new ArrayList<>(composite.getSegments());
        } else {
            return new ArrayList<>(java.util.List.of(route));
        }
    }

    public JTable createLocationTable( BiConsumer<String, Boolean> onLocationSelected,
            boolean isFrom) throws SQLException, IOException, FontFormatException {

        // Custom table model
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Location"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and configure table
        JTable table = new JTable(model);
        table.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Enhanced colors with better contrast
        table.setBackground(Color.WHITE);
        table.setForeground(ThemeManager.getInstance().getBlack());
        table.setSelectionBackground(ThemeManager.getInstance().getYellow());
        table.setSelectionForeground(ThemeManager.getInstance().getBlack());
        table.setGridColor(new Color(240, 240, 240));

        table.setTableHeader(null);

        // Custom cell renderer with alternating colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

                    // Alternating row colors for better readability
                    if (!isSelected) {
                        if (row % 2 == 0) {
                            label.setBackground(Color.WHITE);
                        } else {
                            label.setBackground(new Color(248, 248, 248));
                        }
                    } else {
                        label.setBackground(ThemeManager.getInstance().getYellow());
                        label.setForeground(ThemeManager.getInstance().getBlack());
                        try {
                            label.setFont(Fonts.loadCustomFont(DM_SANS_BOLD, 13));
                        } catch (Exception e) {
                            // Fallback to regular font
                        }
                    }
                }

                return c;
            }
        });

        // Populate table
        List<String> stopNames = getAllStopNames();
        for (String stop : stopNames) {
            model.addRow(new Object[]{stop});
        }

        // Mouse interaction
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String selected = (String) model.getValueAt(row, 0);
                    onLocationSelected.accept(selected, isFrom);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                table.setCursor(Cursor.getDefaultCursor());
            }
        });

        // Hover effect
        table.addMouseMotionListener(new MouseMotionAdapter() {
            private int lastRow = -1;

            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    table.repaint();
                }
            }
        });

        return table;
    }

    public JScrollPane createTableScrollPane(JTable table, Object themeManager) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getInstance().getWhite().darker(), 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        // Style scrollbar
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setBackground(new Color(240, 240, 240));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ThemeManager.getInstance().getBlue().brighter();
                this.trackColor = new Color(240, 240, 240);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        return scrollPane;
    }

    private RouteComponent wrapInComposite(List<RouteComponent> segments) {
        if (segments.isEmpty()) return null;
        Routes composite = new Routes(segments.get(0).getFromLocation() + " to " + segments.get(segments.size()-1).getDestination());
        segments.forEach(composite::addSegment);
        return composite;
    }

    // === INNER CLASSES (unchanged) ===
    public static class SearchResult {
        private final ArrayList<RouteComponent> allRoutes;
        private final ArrayList<RouteComponent> defaultRoute;
        private final boolean success;
        private final String message;

        public SearchResult(ArrayList<RouteComponent> allRoutes,
                            ArrayList<RouteComponent> defaultRoute,
                            boolean success) {
            this.allRoutes = allRoutes;
            this.defaultRoute = defaultRoute;
            this.success = success;
            this.message = "";
        }

        private SearchResult(String message) {
            this.allRoutes = new ArrayList<>();
            this.defaultRoute = null;
            this.success = false;
            this.message = message;
        }

        public static SearchResult empty(String message) {
            return new SearchResult(message);
        }

        public ArrayList<RouteComponent> getAllRoutes() { return allRoutes; }
        public ArrayList<RouteComponent> getDefaultRoute() { return defaultRoute; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public boolean isTransferRoute() { return defaultRoute != null && defaultRoute.size() > 1; }
        public boolean isDirect() { return defaultRoute != null && defaultRoute.size() == 1; }
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
}