import java.util.List;

public record BrickCollisionLogEntry (long updateNumber, double ballBeforeX, double ballBeforeY, double ballBeforeVX,
                                      double ballBeforeVY, List<BrickBoundsSnapshot> overlappingBricks, double surfaceX, double surfaceY, double surfaceWidth, double surfaceHeight,
                                      CollisionAxis axis, double horizontalOverlap, double verticalOverlap, List<BrickHitResult> hitResults,
                                      double ballAfterX, double ballAfterY, double ballAfterVX, double ballAfterVY) {
    public BrickCollisionLogEntry {
        overlappingBricks = List.copyOf(overlappingBricks);
        hitResults = List.copyOf(hitResults);
    }
}
