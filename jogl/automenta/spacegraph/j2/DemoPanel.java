/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacegraph.j2;

import automenta.netention.swing.util.SwingWindow;
import automenta.spacegraph.ui.GridRect;
import automenta.spacegraph.ui.Panel;
import automenta.spacegraph.ui.PointerLayer;

/**
 *
 * @author seh
 */
public class DemoPanel extends AbstractSurfaceDemo {

    @Override
    public String getName() {
        return "2D Fractal Surface";
    }

    @Override
    public String getDescription() {
        return "Zoomable fractal 2D surface.  Multiple adjustable layers.  Adjustable control logic.";
    }

    public DemoPanel() {
        super();

        add(new GridRect(6, 6));

        /* add rectangles, testing:
        --position
        --size
        --color
        --tilt
         */
        int numRectangles = 8;
        float maxRadius = 0.1f;
        for (int i = 0; i < numRectangles; i++) {
            float s = 1.0f + (float) Math.random() * maxRadius;
            float a = (float) i / 1.5f;
            float x = (float) Math.cos(a) * s;
            float y = (float) Math.sin(a) * s;

            Panel r2 = new Panel();
            r2.move(x, y, 0);
            r2.scale(0.6f, 0.3f);
            add(r2);

        }

        add(new PointerLayer(this));
    }

    public static void main(String[] args) {
        new SwingWindow(AbstractSurfaceDemo.newPanel(new DemoPanel()), 800, 800, true);
    }
}
