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

public class SingleExample {
    public static void main(String[] args) {
        HugeClient hugeClient = new HugeClientBuilder("http://localhost:8088", "hugegraph").build();
        SchemaManager schema = hugeClient.schema();
        schema.indexLabel("airportByCountry").onV("airport").by("country").secondary().ifNotExist().create();
        hugeClient.close();
    }
}
