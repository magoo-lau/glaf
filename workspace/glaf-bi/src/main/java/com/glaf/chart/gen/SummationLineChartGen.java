/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glaf.chart.gen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.glaf.chart.domain.Chart;
import com.glaf.chart.util.ChartUtils;
import com.glaf.core.base.ColumnModel;

public class SummationLineChartGen implements ChartGen {
	protected static final Log logger = LogFactory
			.getLog(SummationLineChartGen.class);

	public static void main(String[] paramArrayOfString) {
		SummationLineChartGen chartDemo = new SummationLineChartGen();
		Chart chartModel = new Chart();
		chartModel.setChartFont("����");
		chartModel.setChartFontSize(18);
		chartModel.setChartHeight(800);
		chartModel.setChartWidth(1800);
		chartModel.setChartTitle("������������ͼ");
		chartModel.setImageType("png");
		chartModel.setChartName("line");
		chartModel.setChartType("line");
		chartModel.setCoordinateX("����");
		chartModel.setCoordinateY("����");
		java.util.Random rand = new java.util.Random();

		for (int i = 1; i <= 30; i++) {
			System.out.println("---------------------------");
			ColumnModel cell1 = new ColumnModel();
			cell1.setCategory(String.valueOf(i));
			cell1.setIntValue(i);
			cell1.setColumnName("col_" + i);
			cell1.setSeries("Cross");
			if (i <= 20) {
				cell1.setDoubleValue(rand.nextInt(50) * 1.0D);
			}

			chartModel.addCellData(cell1);
			System.out.println(cell1.getDoubleValue());

			ColumnModel cell2 = new ColumnModel();
			cell2.setColumnName("col2_" + i);
			cell2.setCategory(String.valueOf(i));
			cell2.setSeries("Fit");
			if (i <= 20) {
				cell2.setDoubleValue(rand.nextInt(50) * 1.0D);
			}
			chartModel.addCellData(cell2);
			System.out.println(cell2.getDoubleValue());

			ColumnModel cell3 = new ColumnModel();
			cell3.setColumnName("col3_" + i);
			cell3.setCategory(String.valueOf(i));
			cell3.setSeries("Accord");
			if (i <= 20) {
				cell3.setDoubleValue((94.999999D - cell2.getDoubleValue() - cell1
						.getDoubleValue()) * 1.0D);
			}
			if (i == 10) {
				cell3.setDoubleValue(null);
			}
			chartModel.addCellData(cell3);
			System.out.println(cell3.getDoubleValue());
		}

		JFreeChart chart = chartDemo.createChart(chartModel);
		ChartUtils.createChart(".", chartModel, chart);
	}

	public JFreeChart createChart(Chart chartModel) {
		logger.debug("------------SummationLineChartGen.createChart-----------------");
		ChartUtils.setChartTheme(chartModel);
		CategoryDataset categoryDataset = this.createDataset(chartModel);
		JFreeChart localJFreeChart = ChartFactory.createLineChart(
				chartModel.getChartTitle(), chartModel.getCoordinateX(),
				chartModel.getCoordinateY(), categoryDataset,
				PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart
				.getPlot();
		localCategoryPlot.setRangeZeroBaselineVisible(false);
		NumberAxis localNumberAxis = (NumberAxis) localCategoryPlot
				.getRangeAxis();
		localNumberAxis.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer) localCategoryPlot
				.getRenderer();

		localLineAndShapeRenderer.setBaseShapesVisible(true);
		localLineAndShapeRenderer.setDrawOutlines(true);
		localLineAndShapeRenderer.setUseFillPaint(true);
		localLineAndShapeRenderer.setBaseItemLabelsVisible(true);// ��ʾ��ֵ
		localLineAndShapeRenderer.setBaseFillPaint(Color.white);

		for (int i = 0; i < 8; i++) {
			localLineAndShapeRenderer.setSeriesStroke(i, new BasicStroke(6.0F));
			localLineAndShapeRenderer.setSeriesOutlineStroke(i,
					new BasicStroke(4.0F));
			localLineAndShapeRenderer.setSeriesShape(i, new Ellipse2D.Double(
					-5.0D, -5.0D, 10.0D, 10.0D));
		}

		localLineAndShapeRenderer
				.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

		localNumberAxis.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		localNumberAxis.setAutoRangeIncludesZero(false);
		localNumberAxis.setUpperMargin(0.12D);

		return localJFreeChart;
	}

	public CategoryDataset createDataset(Chart chartModel) {
		DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
		Map<String, Double> total = new HashMap<String, Double>();
		for (ColumnModel cell : chartModel.getColumns()) {
			if (cell.getSeries() != null && cell.getCategory() != null) {
				Double value = total.get(cell.getSeries());
				if (value == null) {
					value = 0D;
				}
				if (cell.getDoubleValue() != null) {
					value += cell.getDoubleValue();
					total.put(cell.getSeries(), value);
					localDefaultCategoryDataset.addValue(value,
							cell.getSeries(), cell.getCategory());
				} else {
					localDefaultCategoryDataset.addValue(null,
							cell.getSeries(), cell.getCategory());
				}
			}
		}
		return localDefaultCategoryDataset;
	}

}