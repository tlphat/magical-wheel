package fit.apcs.magicalwheel.client.timer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import fit.apcs.magicalwheel.client.view.panel.GamePanel;

public class RoundTimerTask extends TimerTask {

    private final AtomicInteger curTime;
    private final GamePanel panel;
    private final Timer timer;

    public RoundTimerTask(int countdown, GamePanel panel, Timer timer) {
        curTime = new AtomicInteger(countdown);
        this.panel = panel;
        this.timer = timer;
    }

    @Override
    public void run() {
        final var time = curTime.decrementAndGet();
        if (time < 0) {
            return;
        }
        SwingUtilities.invokeLater(() -> panel.setTime(time));
        if (time == 0) {
            panel.disableSubmitButton();
            timer.cancel();
        }
    }

}
