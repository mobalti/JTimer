package app;

public enum View {
    POMODORO("pomodoroPage.fxml"),
    STOPWATCH("stopwatchPage.fxml"),
    TIMER("timerPage.fxml"),
    REPORT("reportPage.fxml");

    private final String filename;
    View(String filename) {
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }
}
