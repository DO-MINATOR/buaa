package com.wsp

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object Pagerank {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\hadoop-3.0.0")
    Logger.getLogger("org").setLevel(Level.ERROR)
    val conf = new SparkConf().setAppName("Pagerank").setMaster("local")
    val sc = new SparkContext(conf)

    //构造RDD，links元素类型为(pageId,linkList),代表pageId页面指向linkList中所有的页面
    val links = sc.parallelize(
      Array(("A", List("B", "C", "D")),
        ("B", List("A")),
        ("C", List("A", "B")),
        ("D", List("B", "C"))),
      1)

    //构造RDD，ranks元素类型为(pageId,rank),并初始化每个页面的排序值为1.0
    var ranks = links.mapValues(x => 1.0)

    //迭代计算10次，每次计算过程如下：
    //首先将links和ranks进行拼接，得到(pageId,(linkList,rank))数据集，接着通过map运算得出pageId对应页面对其出链集合中各个页面的贡献值，
    //计算公式为rank/linkList.size。再通过reduceByKey算子得出该轮计算中总的rank值。最后将该页面排序值设为0.15 + 0.85 * contributionsReceived。
    //重复10轮计算，每轮打印出当前结果
    for (i <- 0 until 10) {
      val contributions = links.join(ranks).flatMap {
        case (pageId, (linkList, rank)) =>
          linkList.map(link => (link, rank / linkList.size))
      }
      ranks = contributions
        .reduceByKey((a, b) => a + b)
        .mapValues(rank => 0.15 + 0.85 * rank)
      print("iter " + i + ":\n")
      ranks.collect().foreach(println)
      println()
    }
  }
}
