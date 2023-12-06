package cn.xlearning.hugegraph;

import org.apache.hugegraph.driver.GraphManager;
import org.apache.hugegraph.driver.HugeClient;
import org.apache.hugegraph.driver.SchemaManager;
import org.apache.hugegraph.structure.graph.Edge;
import org.apache.hugegraph.structure.graph.Edges;
import org.apache.hugegraph.structure.graph.Vertex;
import org.apache.hugegraph.util.E;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.StringComponentNameProvider;
import org.jgrapht.io.VertexProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AirRoutesImport {
    public static void main(String[] args) throws IOException, ImportException {
        HugeClient hugeClient = HugeClient.builder("http://localhost:8088",
                        "hugegraph")
                .build();
//        SchemaManager schema = hugeClient.schema();
//        schema.propertyKey("type").asText().ifNotExist().create();
//        schema.propertyKey("code").asText().ifNotExist().create();
//        schema.propertyKey("icao").asText().ifNotExist().create();
//        schema.propertyKey("desc").asText().ifNotExist().create();
//        schema.propertyKey("region").asText().ifNotExist().create();
//        schema.propertyKey("runways").asInt().ifNotExist().create();
//        schema.propertyKey("longest").asInt().ifNotExist().create();
//        schema.propertyKey("elev").asInt().ifNotExist().create();
//        schema.propertyKey("country").asText().ifNotExist().create();
//        schema.propertyKey("city").asText().ifNotExist().create();
//        schema.propertyKey("lat").asDouble().ifNotExist().create();
//        schema.propertyKey("lon").asDouble().ifNotExist().create();
//        schema.propertyKey("dist").asInt().ifNotExist().create();
//        schema.propertyKey("number").asInt().ifNotExist().create();
//
//        schema.vertexLabel("continent").properties("type", "code", "desc").useCustomizeNumberId().ifNotExist().create();
//        schema.vertexLabel("country").properties("type", "code", "desc").useCustomizeNumberId().ifNotExist().create();
//        schema.vertexLabel("airport").properties("type", "code", "icao", "desc", "region", "runways", "longest", "elev", "country", "city", "lat", "lon").useCustomizeNumberId().ifNotExist().create();
//        schema.edgeLabel("route").properties("number","dist").sourceLabel("airport").targetLabel("airport").ifNotExist().create();
//        schema.edgeLabel("contains").properties("number").sourceLabel("country").targetLabel("airport").ifNotExist().create();
//        schema.vertexLabel("version").properties("type", "code", "desc").useCustomizeNumberId().ifNotExist().create();
//        schema.edgeLabel("include").properties("number").sourceLabel("continent").targetLabel("airport").ifNotExist().create();
//
//        schema.indexLabel("continentByCode").onV("continent").by("code").secondary().ifNotExist().create();
//        schema.indexLabel("countryByCode").onV("country").by("code").secondary().ifNotExist().create();
//        schema.indexLabel("airportByCode").onV("airport").by("code").secondary().ifNotExist().create();
//        schema.indexLabel("airportByRegion").onV("airport").by("region").secondary().ifNotExist().create();
//        schema.indexLabel("routeByNumber").onE("route").by("number").secondary().ifNotExist().create();
//        schema.indexLabel("containsByNumber").onE("contains").by("number").secondary().ifNotExist().create();
//        schema.indexLabel("includeByNumber").onE("include").by("number").secondary().ifNotExist().create();
//        schema.indexLabel("airportByCity").onV("airport").by("city").secondary().ifNotExist().create();
//        schema.indexLabel("airportByCountry").onV("airport").by("country").secondary().ifNotExist().create();
//        schema.indexLabel("airportByRunways").onV("airport").by("runways").range().ifNotExist().create();

        GraphManager graph = hugeClient.graph();
//        List<Vertex> vertices = new ArrayList<>();
//        BufferedReader nodes = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/air-routes-latest-nodes.txt"));
//        String line = nodes.readLine();
//        while ((line = nodes.readLine()) != null) {
//            String[] properties = line.split("\t");
//            if (properties[1].equals("airport")) {
//                System.out.println(line);
//                Vertex vertex = new Vertex("airport").property("type", properties[2])
//                        .property("code", properties[3]).property("icao", properties[4])
//                        .property("desc", properties[5]).property("region", properties[6])
//                        .property("runways", Integer.parseInt(properties[7])).property("longest", Integer.parseInt(properties[8]))
//                        .property("elev", Integer.parseInt(properties[9])).property("country", properties[10])
//                        .property("city", properties[11]).property("lat", Double.parseDouble(properties[12]))
//                        .property("lon", Double.parseDouble(properties[13]));
//                vertex.id(Integer.parseInt(properties[0]));
//                vertices.add(vertex);
//            } else if (properties[1].equals("country")) {
//                Vertex vertex = new Vertex("country").property("type", properties[2])
//                        .property("code", properties[3]).property("desc", properties[5]);
//                vertex.id(Integer.parseInt(properties[0]));
//                vertices.add(vertex);
//            } else if (properties[1].equals("continent")) {
//                Vertex vertex = new Vertex("continent").property("type", properties[2])
//                        .property("code", properties[3]).property("desc", properties[5]);
//                vertex.id(Integer.parseInt(properties[0]));
//                vertices.add(vertex);
//            } else if (properties[1].equals("version")) {
//                Vertex vertex = new Vertex("version").property("type", properties[2])
//                        .property("code", properties[3]).property("desc", properties[5]);
//                vertex.id(Integer.parseInt(properties[0]));
//                vertices.add(vertex);
//            }
//            if(vertices.size() >= 500){
//                graph.addVertices(vertices);
//                vertices.clear();
//            }
//        }
//
//        nodes.close();
//        graph.addVertices(vertices);
        List<Edge> edges = new ArrayList<>();
        BufferedReader edgesList = new BufferedReader(new FileReader("/Users/zhangjun/code-open/graph/sample-data/air-routes-latest-edges.csv"));
        String line = edgesList.readLine();
        while ((line = edgesList.readLine()) != null) {
            String[] properties = line.split(",");
            if (properties[3].equals("route")) {
                Edge edge = new Edge("route").property("number", Integer.parseInt(properties[0]));
                edge.sourceLabel("airport");
                edge.targetLabel("airport");
                edge.sourceId(Integer.parseInt(properties[1]));
                edge.targetId(Integer.parseInt(properties[2]));
                edge.property("dist", Integer.parseInt(properties[4]));
                edges.add(edge);
            } else if(properties[3].equals("contains")) {
                Vertex vertex = graph.getVertex(Integer.parseInt(properties[1]));
                System.out.println(vertex.label());
                if(vertex.label().equals("country")){
                    Edge edge = new Edge("contains").property("number", Integer.parseInt(properties[0]));
                    edge.sourceLabel("country");
                    edge.targetLabel("airport");
                    edge.sourceId(Integer.parseInt(properties[1]));
                    edge.targetId(Integer.parseInt(properties[2]));
                    edges.add(edge);
                }else{
                    Edge edge = new Edge("include").property("number", Integer.parseInt(properties[0]));
                    edge.sourceLabel("continent");
                    edge.targetLabel("airport");
                    edge.sourceId(Integer.parseInt(properties[1]));
                    edge.targetId(Integer.parseInt(properties[2]));
                    edges.add(edge);
                }

            }
            if(edges.size() >= 500) {
                graph.addEdges(edges);
                edges.clear();
            }
        }
        edgesList.close();
        graph.addEdges(edges);
        hugeClient.close();
    }
}
