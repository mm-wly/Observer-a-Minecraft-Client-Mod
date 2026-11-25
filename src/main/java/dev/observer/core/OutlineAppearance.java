package dev.observer.core;

/** Single-pass depth cue, independent of the graphics backend. */
public record OutlineAppearance(int color,float width) {
    public static OutlineAppearance at(Box box,boolean approximate,double x,double y,double z) {
        double dx=Math.max(0,Math.max(box.minX()-x,x-box.maxX())),dy=Math.max(0,Math.max(box.minY()-y,y-box.maxY())),dz=Math.max(0,Math.max(box.minZ()-z,z-box.maxZ()));
        // Half strength at 32 blocks; retain a readable outline at long range.
        double fade=1/(1+(dx*dx+dy*dy+dz*dz)/(32*32));
        int alpha=(int)Math.round(100+150*fade);
        return new OutlineAppearance((alpha<<24)|(approximate?0xff4545:0x85e5f2),(float)(1.5+1.5*fade));
    }
}
