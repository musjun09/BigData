package cn.xlearning.graph

import org.apache.spark.graphx.{Edge, EdgeDirection, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.util.GraphGenerators
import java.io.Serializable

object GraphTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SimpleGraphX").setMaster("local[2]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val g = Graph(sc.makeRDD((1L to 7L).map((_,""))),
      sc.makeRDD(Array(Edge(2L,5L,""), Edge(5L,3L,""), Edge(3L,2L,""),
        Edge(4L,5L,""), Edge(6L,7L,"")))).cache
      g.connectedComponents().vertices.foreach(println)
//    g.connectedComponents.vertices.map(_.swap).groupByKey.map(_._2).collect.foreach(println _)
//    //增强连通图算法
//    g.stronglyConnectedComponents(10).vertices.map(_.swap).groupByKey.map(_._2).collect.foreach(println _)

    sc.stop()
  }
}
case class User(name: String, age: Int, inDeg: Int, outDeg: Int) extends Serializable
