public record BrickCollisionSnapshot (int x, int y, int width, int height,
                               CollisionAxis collisionAxis, double horizontalOverlap,
                               double verticalOverlap, boolean destroyed) {

}
