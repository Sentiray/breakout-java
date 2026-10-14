import java.util.List;

public record BrickCollisionLogEntry (long updateNumber, double ballBeforeX, double ballBeforeY, double ballBeforeVX,
                                   double ballBeforeVY, List<BrickBoundsSnapshot> overlappingBricks, int brickX, int brickY,
                                   CollisionAxis axis, double horizontalOverlap, double verticalOverlap, boolean destroyed,
                                   double ballAfterX, double ballAfterY, double ballAfterVX, double ballAfterVY) {
    public BrickCollisionLogEntry {
        overlappingBricks = List.copyOf(overlappingBricks);
    }
}
