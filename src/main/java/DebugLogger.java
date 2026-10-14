import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class DebugLogger {
    private BufferedWriter writer;
    private String csvHeader;

    public DebugLogger() {

        this.csvHeader = "updateNumber,ballBeforeX,ballBeforeY,ballBeforeVX,ballBeforeVY," +
                "overlappingBricks,brickX,brickY,axis,horizontalOverlap,verticalOverlap," +
                "destroyed,ballAfterX,ballAfterY,ballAfterVX,ballAfterVY";

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
            for (BrickBoundsSnapshot brick : entry.overlappingBricks()) {
                brickData.append(brick.x() + ":" + brick.y() + ":" +  brick.width() + ":" + brick.height() + ";");
            }
            String brickResult = brickData.toString();
            String csvEntry = getCsvRow(entry, brickResult);
            writer.write(csvEntry + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCsvRow(BrickCollisionLogEntry entry, String brickData) {
        return entry.updateNumber() + "," + entry.ballBeforeX() + "," + entry.ballBeforeY() + "," + entry.ballBeforeVX() + "," +
                                         entry.ballBeforeVY() + "," + brickData + "," + entry.brickX() + "," + entry.brickY() + "," + entry.axis() + "," + entry.horizontalOverlap() + "," +
                                         entry.verticalOverlap() + "," + entry.destroyed() + "," + entry.ballAfterX() + "," + entry.ballAfterY() + "," + entry.ballAfterVX() + "," + entry.ballAfterVY();
    }
}
