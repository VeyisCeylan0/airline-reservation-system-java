package ServicesAndManagers;

public interface ReportListener {
    void onReportStatus(String message);
    void onReportReady(String reportText);
}
