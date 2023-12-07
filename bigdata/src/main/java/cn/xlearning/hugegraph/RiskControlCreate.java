package cn.xlearning.hugegraph;

import org.apache.hugegraph.driver.GraphManager;
import org.apache.hugegraph.driver.HugeClient;
import org.apache.hugegraph.driver.SchemaManager;
import org.apache.hugegraph.structure.graph.Edge;
import org.apache.hugegraph.structure.graph.Vertex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiskControlCreate {
    public static void main(String[] args) throws IOException {
        HugeClient hugeClient = HugeClient.builder("http://localhost:8088", "risk").build();
//        createSchema(hugeClient);
//        createVertex(hugeClient);
        createEdge(hugeClient);
        hugeClient.close();
    }
    public static void createEdge(HugeClient hugeClient) throws IOException {
        GraphManager graph = hugeClient.graph();
        List<Edge> edges = new ArrayList<>();
        BufferedReader edgesList = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/phone2phone.txt"));
        String line = edgesList.readLine();
        while ((line = edgesList.readLine()) != null) {
            String[] properties = line.split(",");
            Edge edge = new Edge("call").property("startTime", properties[2]).property("endTime", properties[3]);
            edge.sourceLabel("phone");
            edge.targetLabel("phone");
            edge.sourceId(properties[0].trim());
            edge.targetId(properties[1].trim());
            edges.add(edge);
            if(edges.size() >= 500) {
                graph.addEdges(edges);
                edges.clear();
            }
        }
        if(edges.size() > 0){
            graph.addEdges(edges);
        }
        edges.clear();

        edgesList = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/person.txt"));
        line = edgesList.readLine();
        while ((line = edgesList.readLine()) != null) {
            String[] properties = line.split(",");
            Edge edge = new Edge("has_phone");
            edge.sourceLabel("person");
            edge.targetLabel("phone");
            edge.sourceId(properties[0].trim());
            edge.targetId(properties[3].trim());
            edges.add(edge);
            if(edges.size() >= 500) {
                graph.addEdges(edges);
                edges.clear();
            }
        }
        if(edges.size() > 0){
            graph.addEdges(edges);
        }
        edges.clear();

        edgesList = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/apply_train.txt"));
        line = edgesList.readLine();
        while ((line = edgesList.readLine()) != null) {
            String[] properties = line.split(",");
            Edge edge = new Edge("has_application");
            edge.sourceLabel("person");
            edge.targetLabel("application");
            edge.sourceId(properties[8].trim());
            edge.targetId(properties[0].trim());
            edges.add(edge);

            Edge edge1 = new Edge("parent_phone");
            edge1.sourceLabel("application");
            edge1.targetLabel("phone");
            edge1.sourceId(properties[0].trim());
            edge1.targetId(properties[5].trim());
            edges.add(edge1);

            Edge edge2 = new Edge("colleague_phone");
            edge2.sourceLabel("application");
            edge2.targetLabel("phone");
            edge2.sourceId(properties[0].trim());
            edge2.targetId(properties[6].trim());
            edges.add(edge2);

            Edge edge3 = new Edge("company_phone");
            edge3.sourceLabel("application");
            edge3.targetLabel("phone");
            edge3.sourceId(properties[0].trim());
            edge3.targetId(properties[7].trim());
            edges.add(edge3);

            if(edges.size() >= 500) {
                graph.addEdges(edges);
                edges.clear();
            }
        }
        edgesList.close();
        if(edges.size() > 0){
            graph.addEdges(edges);
        }
        edges.clear();
    }
    public static void createVertex(HugeClient hugeClient) throws IOException {
        GraphManager graph = hugeClient.graph();
        List<Vertex> vertices = new ArrayList<>();
        BufferedReader nodes = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/phone.txt"));
        String line = nodes.readLine();
        while ((line = nodes.readLine()) != null) {
            String[] properties = line.split(",");
            Vertex vertex = new Vertex("phone").property("phoneFlag", properties[1].trim());
            vertex.id(properties[0].trim());
            vertices.add(vertex);
            if(vertices.size() >= 500){
                graph.addVertices(vertices);
                vertices.clear();
            }
        }
        nodes.close();
        if(vertices.size() > 0){
            graph.addVertices(vertices);
        }
        vertices.clear();

        nodes = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/person.txt"));
        line = nodes.readLine();
        while ((line = nodes.readLine()) != null) {
            String[] properties = line.split(",");
            Vertex vertex = new Vertex("person").property("name", properties[1].trim())
                            .property("sex", properties[2].trim()).property("flag", properties[4].trim());
            vertex.id(properties[0].trim());
            vertices.add(vertex);
            if(vertices.size() >= 500){
                graph.addVertices(vertices);
                vertices.clear();
            }
        }
        nodes.close();
        if(vertices.size() > 0){
            graph.addVertices(vertices);
        }

        vertices.clear();

        nodes = new BufferedReader(new FileReader("/Users/zhangjun/Downloads/知识图谱/项目作业2/apply_train.txt"));
        line = nodes.readLine();
        while ((line = nodes.readLine()) != null) {
            String[] properties = line.split(",");
            Vertex vertex = new Vertex("application").property("amount", Long.parseLong(properties[1].trim()))
                    .property("term", Integer.parseInt(properties[2].trim())).property("job", properties[3].trim())
                    .property("city", properties[4].trim()).property("status", properties[9].trim());
            vertex.id(properties[0].trim());
            vertices.add(vertex);
            if(vertices.size() >= 500){
                graph.addVertices(vertices);
                vertices.clear();
            }
        }
        nodes.close();
        if(vertices.size() > 0){
            graph.addVertices(vertices);
        }
    }
    public static void createSchema(HugeClient hugeClient){
        SchemaManager schema = hugeClient.schema();
        schema.propertyKey("phoneFlag").asText().ifNotExist().create();
        schema.propertyKey("name").asText().ifNotExist().create();
        schema.propertyKey("sex").asText().ifNotExist().create();
        schema.propertyKey("flag").asText().ifNotExist().create();
        schema.propertyKey("amount").asLong().ifNotExist().create();
        schema.propertyKey("term").asInt().ifNotExist().create();
        schema.propertyKey("job").asText().ifNotExist().create();
        schema.propertyKey("city").asText().ifNotExist().create();
        schema.propertyKey("status").asText().ifNotExist().create();
        schema.propertyKey("startTime").asDate().ifNotExist().create();
        schema.propertyKey("endTime").asDate().ifNotExist().create();

        schema.vertexLabel("phone").properties("phoneFlag").useCustomizeStringId().ifNotExist().create();
        schema.vertexLabel("person").properties("name", "sex", "flag").useCustomizeStringId().ifNotExist().create();
        schema.vertexLabel("application").properties("amount", "term", "job", "city", "status").useCustomizeStringId().ifNotExist().create();

        schema.edgeLabel("has_phone").sourceLabel("person").targetLabel("phone").ifNotExist().create();
        schema.edgeLabel("parent_phone").sourceLabel("application").targetLabel("phone").ifNotExist().create();
        schema.edgeLabel("colleague_phone").sourceLabel("application").targetLabel("phone").ifNotExist().create();
        schema.edgeLabel("company_phone").sourceLabel("application").targetLabel("phone").ifNotExist().create();
        schema.edgeLabel("has_application").sourceLabel("person").targetLabel("application").ifNotExist().create();
        schema.edgeLabel("call").properties("startTime", "endTime").sourceLabel("phone").targetLabel("phone").ifNotExist().create();
    }
}
