/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.swing;

import automenta.netention.Link;
import automenta.netention.Node;
import automenta.netention.graph.ValueEdge;
import automenta.netention.impl.MemorySelf;
import automenta.netention.linker.MetadataGrapher;
import automenta.netention.swing.RunDemos.Demo;
import automenta.netention.swing.util.SwingWindow;
import automenta.spacegraph.SGCanvas;
import automenta.spacegraph.SGPanel;
import com.syncleus.dann.graph.DirectedEdge;
import com.syncleus.dann.graph.MutableBidirectedGraph;
import com.syncleus.dann.graph.MutableDirectedAdjacencyGraph;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javolution.context.ConcurrentContext;

/**
 *
 * @author seh
 */
public class RunSelfGraphPanel  extends SGCanvas implements Demo {


    public static void main(String[] args) {        
        SwingWindow sw = new SwingWindow(new RunSelfGraphPanel().newPanel(), 400, 400, true);
    }

    @Override
    public String getName() {
        return "Self Graph";
    }

    @Override
    public String getDescription() {
        return "..";
    }

    @Override
    public JPanel newPanel() {
        ConcurrentContext.setConcurrency(Runtime.getRuntime().availableProcessors());

        MemorySelf self = new MemorySelf("me", "Me");
        new SeedSelfBuilder().build(self);

        //self.addPlugin(new Twitter());

        self.updateLinks(null);

        MutableBidirectedGraph<Node,ValueEdge<Node, Link>> target = new MutableDirectedAdjacencyGraph(self.getGraph());
        MetadataGrapher.run(self, target, true, true, true, true);

        JPanel j = new SGPanel(new GraphCanvas(target, 3));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(j, BorderLayout.CENTER);
        panel.add(new JButton("X"), BorderLayout.SOUTH);

        return panel;

    }
}