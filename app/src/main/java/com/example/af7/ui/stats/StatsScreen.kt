package com.example.af7.ui.stats

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.af7.ui.MainViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun StatsScreen(viewModel: MainViewModel) {
    val todos by viewModel.todos.collectAsState()
    val totalCount = todos.size
    val completedCount = todos.count { it.completed }
    val incompleteCount = totalCount - completedCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Estadísticas de Tareas",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total: $totalCount")
                Text("Completadas: $completedCount", color = Color(0xFF4CAF50))
                Text("Pendientes: $incompleteCount", color = Color(0xFFF44336))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (totalCount > 0) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { context ->
                    PieChart(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        description.isEnabled = false
                        isDrawHoleEnabled = true
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        legend.orientation = Legend.LegendOrientation.VERTICAL
                        legend.setDrawInside(false)
                    }
                },
                update = { chart ->
                    val entries = ArrayList<PieEntry>()
                    entries.add(PieEntry(completedCount.toFloat(), "Completadas"))
                    entries.add(PieEntry(incompleteCount.toFloat(), "Pendientes"))

                    val dataSet = PieDataSet(entries, "Estado Tareas")
                    // android.graphics.Color
                    dataSet.colors = listOf(
                        android.graphics.Color.parseColor("#4CAF50"),
                        android.graphics.Color.parseColor("#F44336")
                    )
                    dataSet.valueTextSize = 14f
                    dataSet.valueTextColor = android.graphics.Color.WHITE
                    
                    val pData = PieData(dataSet)
                    chart.data = pData
                    chart.invalidate() // refresh
                }
            )
        } else {
            Text("No hay datos suficientes para generar un gráfico.")
        }
    }
}
