package cn.xlearning.hugegraph;


import org.apache.hugegraph.driver.GraphManager;
import org.apache.hugegraph.driver.GremlinManager;
import org.apache.hugegraph.driver.HugeClient;
import org.apache.hugegraph.driver.HugeClientBuilder;
import org.apache.hugegraph.driver.SchemaManager;
import org.apache.hugegraph.structure.graph.Edge;
import org.apache.hugegraph.structure.graph.Path;
import org.apache.hugegraph.structure.graph.Vertex;
import org.apache.hugegraph.structure.gremlin.Result;
import org.apache.hugegraph.structure.gremlin.ResultSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SingleExample {
    public static void main(String[] args) {
        HugeClient hugeClient = new HugeClientBuilder("http://localhost:8088", "risk").build();
        GremlinManager gremlin = hugeClient.gremlin();
        System.out.println("==== Path ====");
        ResultSet resultSet = gremlin.gremlin("g.V().hasLabel('person').as('a').out('has_phone').has('phoneFlag', 'BLACK').as('b').select('a', 'b').by('name').by(id)").execute();
        Iterator<Result> results = resultSet.iterator();
        results.forEachRemaining(result -> {
            Map<String, String> map = (Map<String, String>) result.getObject();
            System.out.println(map.get("a") + "->" + map.get("b"));
        });
        hugeClient.close();
    }
}
