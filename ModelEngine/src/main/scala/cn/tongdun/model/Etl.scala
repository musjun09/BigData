package cn.tongdun.model

import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame
import org.apache.hugegraph.driver.{GremlinManager, HugeClient, HugeClientBuilder}
import org.apache.spark.sql.SparkSession

import java.io.{File, PrintStream}

object Etl {
  def main(args: Array[String]): Unit = {
    val hugeClient = new HugeClientBuilder("http://localhost:8088", "risk").build
    val gremlin = hugeClient.gremlin
    val sc:SparkSession = SparkSession.builder().appName("etl").master("local[2]")
      .config("spark.sql.shuffle.partitions",4).getOrCreate()

    val trainData = new PrintStream(new File("/Users/zhangjun/Downloads/知识图谱/项目作业2/test.txt"))
    val data = sc.sparkContext.textFile("/Users/zhangjun/Downloads/知识图谱/项目作业2/apply_test.txt")
      .map(x => {
        val properties = x.split(",")
        val id = properties(0)
        val amount = properties(1).toLong
        val term = properties(2).toInt
        val parentPhone = properties(5)
        val personId = properties(8)
//        val status = properties(9)
        (id, amount / term, parentPhone, personId)
      }).collect().map{
      case (id, amount, parentPhone, personId) => {
        //进件人有无逾期
        val overdueCount = gremlin.gremlin(String.format("g.V('%s').out('has_application').has('status', 'OVERDUE').count()", personId))
          .execute().iterator().next().getInt
        //进件人状态
        val flag = gremlin.gremlin(String.format("g.V('%s').values('flag')", personId))
          .execute().iterator().next().getString
        //进件人是否有黑名单号码
        val phoneFlag = gremlin.gremlin(String.format("g.V('%s').out('has_phone').has('phoneFlag', 'BLACK').count()", personId))
          .execute().iterator().next().getInt

        //进件人父母手机号状态
        val parentPhoneFlag = gremlin.gremlin(String.format("g.V('%s').values('phoneFlag')", parentPhone))
          .execute().iterator().next().getString

        amount + "," + flag + "," + phoneFlag + "," + parentPhoneFlag + "," + overdueCount
      }
    }.foreach(x => trainData.println(x))
    trainData.close()
    sc.stop()
  }
}
