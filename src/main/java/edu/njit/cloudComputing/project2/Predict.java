package edu.njit.cloudComputing.project2;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Predict {
    public static void main( String[] args ) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Predict")
                .master("local")
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

        Dataset<Row> validationDataFrame = spark.read().format("csv")
                .schema(schema)
                .option("header", "true")
                .option("sep", ";")
                .load("ValidationDataset.csv");

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

        Dataset<Row> vectorDataFrame = vectorAssembler.transform(validationDataFrame);

        StringIndexer indexer = new StringIndexer().setInputCol("quality").setOutputCol("label");
        Dataset<Row> validation = indexer.fit(vectorDataFrame).transform(vectorDataFrame);

        LogisticRegressionModel logisticRegressionModel = LogisticRegressionModel.read().load("model");

        Dataset<Row> predictions = logisticRegressionModel.transform(validation);

        predictions.select("prediction", "label", "features").show(10);

        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");

        double accuracy = evaluator.evaluate(predictions);
        MulticlassMetrics rm2 = new MulticlassMetrics(predictions.select("prediction", "label"));

        System.out.println(">>> Accuracy = " + accuracy);
        System.out.println(">>> F1 = " + rm2.weightedFMeasure());

        spark.stop();
    }

}
