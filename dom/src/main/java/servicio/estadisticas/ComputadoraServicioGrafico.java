/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package servicio.estadisticas;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Named;

import com.danhaywood.isis.wicket.wickedcharts.applib.WickedChart;
import com.google.common.collect.Maps;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.DataLabels;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.HighchartsColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.color.RadialGradient;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageFormatter;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;

import dom.computadora.Computadora;
import dom.computadora.Computadora.CategoriaDisco;
import dom.computadora.ComputadoraRepositorio;

@Named("Estadisticas")
public class ComputadoraServicioGrafico  {
//Para probar voy a realizar un grafico que muestre todas las computadoras por sector.
    @ActionSemantics(Of.SAFE)
    public WickedChart filtrarPorDiscoRigido() {
        
        Map<CategoriaDisco, AtomicInteger> byCategory = Maps.newTreeMap();
        List<Computadora> allToDos = toDoItems.listar();
        for (Computadora unaComputadora : allToDos) {
            CategoriaDisco category = unaComputadora.getDisco();
            AtomicInteger integer = byCategory.get(category);
            if(integer == null) {
                integer = new AtomicInteger();
                byCategory.put(category, integer);
            }
            integer.incrementAndGet();
        }
        
        return new WickedChart(new PieWithGradientOptions(byCategory));
    }
    
    public static class PieWithGradientOptions extends Options {
        private static final long serialVersionUID = 1L;

        public PieWithGradientOptions(Map<CategoriaDisco, AtomicInteger> byCategory) {
        
            setChartOptions(new ChartOptions()
                .setPlotBackgroundColor(new NullColor())
                .setPlotBorderWidth(null)
                .setPlotShadow(Boolean.FALSE));
            
            setTitle(new Title("Computadoras: Discos Rigidos."));
        
            PercentageFormatter formatter = new PercentageFormatter();
            setTooltip(
                    new Tooltip()
                        .setFormatter(
                                formatter)
                        .       setPercentageDecimals(1));
        
            setPlotOptions(new PlotOptionsChoice()
                .setPie(new PlotOptions()
                .setAllowPointSelect(Boolean.TRUE)
                .setCursor(Cursor.POINTER)
                .setDataLabels(new DataLabels()
                .setEnabled(Boolean.TRUE)
                .setColor(new HexColor("#000000"))
                .setConnectorColor(new HexColor("#000000"))
                .setFormatter(formatter))));

            Series<Point> series = new PointSeries()
                .setType(SeriesType.PIE);
            int i=0;
            for (Map.Entry<CategoriaDisco, AtomicInteger> entry : byCategory.entrySet()) {
                series
                .addPoint(
                        new Point(entry.getKey().name(), entry.getValue().get()).setColor(
                                new RadialGradient()
                                    .setCx(0.5)
                                    .setCy(0.3)
                                    .setR(0.7)
                                        .addStop(0, new HighchartsColor(i))
                                        .addStop(1, new HighchartsColor(i).brighten(-0.3f))));
                i++;
            }
            addSeries(series);
        }
    }

    
    // //////////////////////////////////////
    // Injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    private ComputadoraRepositorio toDoItems;

}
