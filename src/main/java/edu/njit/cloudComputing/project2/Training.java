package edu.njit.cloudComputing.project2;

import org.apache.spark.SparkConf;


import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegressionTrainingSummary;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;


import java.io.IOException;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Training
{
    public static void main( String[] args ) {

        boolean local = true;

        String MASTER_URI = "spark://ip-172-31-28-8.ec2.internal:7077";
        String PATH = "../";

        if(local) {
            System.setProperty("hadoop.home.dir", "D:/hadoop-3.2.2");
            MASTER_URI = "local";
            PATH = "";
        }

        SparkConf conf = new SparkConf().setAppName("Training").setMaster(MASTER_URI);

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        StructType schema = DataTypes.createStructType(new StructField[] {
                        new StructField("fixed acidity", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("volatile acidity", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("citric acid", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("residual sugar", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("chlorides", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("free sulfur dioxide", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("total sulfar dioxide", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("density", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("pH", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("sulphates", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("alcohol", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("quality", DataTypes.IntegerType, true, Metadata.empty()),
                });

        Dataset<Row> trainingDataFrame = spark.read().format("csv")
                .schema(schema)
                .option("header", "true")
                .option("sep", ";")
                .load(PATH + "TrainingDataset.csv");

        String[] featureCols = new String[]{
                "fixed acidity",
                "volatile acidity",
                "citric acid",
                "residual sugar",
                "chlorides",
                "free sulfur dioxide",
                "total sulfar dioxide",
                "density",
                "pH",
                "sulphates",
                "alcohol"
        };

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(featureCols)
                .setOutputCol("features");

        Dataset<Row> vectorDataFrame = vectorAssembler.transform(trainingDataFrame);

        StringIndexer indexer = new StringIndexer().setInputCol("quality").setOutputCol("label");
        Dataset<Row> training = indexer.fit(vectorDataFrame).transform(vectorDataFrame);

        LogisticRegression mlr = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.3)
                .setElasticNetParam(0.8)
                .setFamily("multinomial");

        LogisticRegressionModel mlrModel = mlr.fit(training);

        try {
            mlrModel.write().overwrite().save("model");
        } catch (IOException e) {
            e.printStackTrace();
        }

        spark.stop();

    }

}
