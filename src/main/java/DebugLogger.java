import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class DebugLogger {
    private BufferedWriter writer;
    private String csvHeader;

    public DebugLogger() {

        this.csvHeader = "updateNumber,ballBeforeX,ballBeforeY,ballBeforeVX,ballBeforeVY," +
                "overlappingBricks,surfaceX,surfaceY,surfaceWidth,surfaceHeight,axis,horizontalOverlap,verticalOverlap," +
                "hitResults,ballAfterX,ballAfterY,ballAfterVX,ballAfterVY";

        try {
            FileWriter fileWriter = new FileWriter("brick-collisions.csv");
            this.writer = new BufferedWriter(fileWriter);
            writer.write(csvHeader + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logBrickCollision(BrickCollisionLogEntry entry) {
        if (writer == null) {
            return;
        }
        try {
            StringBuilder brickData = new StringBuilder();
            StringBuilder hitResultData = new StringBuilder();
            for (BrickBoundsSnapshot brick : entry.overlappingBricks()) {
                brickData.append(brick.x() + ":" + brick.y() + ":" +  brick.width() + ":" + brick.height() + ";");
            }
            for (BrickHitResult hitResult : entry.hitResults()) {
                hitResultData.append(hitResult.bounds().x()).append(":").append(hitResult.bounds().y()).append(":").append(hitResult.bounds().width()).append(":").append(hitResult.bounds().height()).append(":").append(hitResult.destroyed()).append(";");
            }
            String brickResult = brickData.toString();
            String hitResult = hitResultData.toString();
            String csvEntry = getCsvRow(entry, brickResult, hitResult);
            writer.write(csvEntry + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCsvRow(BrickCollisionLogEntry entry, String brickData, String hitResult) {
        return entry.updateNumber() + "," + entry.ballBeforeX() + "," + entry.ballBeforeY() + "," + entry.ballBeforeVX() + "," +
                                         entry.ballBeforeVY() + "," + brickData + "," + entry.surfaceX() + "," + entry.surfaceY() + "," + entry.surfaceWidth() + "," + entry.surfaceHeight() + "," + entry.axis() + "," + entry.horizontalOverlap() + "," +
                                         entry.verticalOverlap() + "," + hitResult + "," + entry.ballAfterX() + "," + entry.ballAfterY() + "," + entry.ballAfterVX() + "," + entry.ballAfterVY();
    }
}
