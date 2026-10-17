import java.util.List;

public record BrickCollisionSnapshot (double x, double y, double width, double height,
                                      CollisionAxis collisionAxis, double horizontalOverlap,
                                      double verticalOverlap, List<BrickHitResult> hitResults) {

    public BrickCollisionSnapshot {
        hitResults = List.copyOf(hitResults);
    }

}
