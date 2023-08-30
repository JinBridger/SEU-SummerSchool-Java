package app.vcampus.client.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.vcampus.client.scene.components.SideBar
import app.vcampus.client.scene.components.shadowCustom
import app.vcampus.client.scene.subscene.blankSubscene
import app.vcampus.client.scene.subscene.teachingaffairs.*
import app.vcampus.client.viewmodel.TeachingAffairsViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel


@Composable
fun TeachingAffairsStatusForUser(viewModel: TeachingAffairsViewModel) {
    val teachingAffairsSideBarItem = viewModel.teachingAffairsSideBarItem
    val currentSubscene = remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth()) {
        SideBar(teachingAffairsSideBarItem) {
            (0..<teachingAffairsSideBarItem.size).forEach { i ->
                teachingAffairsSideBarItem[i] = teachingAffairsSideBarItem[i].copy(
                        isChosen = false)
            }
            teachingAffairsSideBarItem[it] = teachingAffairsSideBarItem[it].copy(
                    isChosen = true)
            currentSubscene.value = teachingAffairsSideBarItem[it].heading
        }
        Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth().shadowCustom(
                        offsetX = 3.dp, blurRadius = 10.dp)
                        .background(
                                Color.White
                        )
                        .padding(horizontal = 100.dp)
        ) {
            when (currentSubscene.value) {
                "" -> blankSubscene()
                "我的课表" -> myScheduleSubscene(viewModel)
                "我的成绩" -> myGradeSubscene(viewModel)
                "我的课堂" -> myClassSubscene(viewModel)
                "评教结果" -> evaluateResultSubscene(viewModel)
                "选课" -> chooseClassSubscene(viewModel)
                "评教" -> evaluateTeacherSubscene(viewModel)
                "排课" -> arrangeClassSubscene(viewModel)
                "录入成绩" -> enterScoreSubscene(viewModel)
            }
//            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
//
//                LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                    item {
//                        Spacer(Modifier.height(80.dp))
//                        pageTitle("教务处", "教务信息")
//                    }
//                }
//            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun TeachingAffairsScene(
        navi: Navigator
) {
    val viewModel = viewModel(TeachingAffairsViewModel::class, listOf()) {
        TeachingAffairsViewModel()
    }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

//    Scaffold(scaffoldState = scaffoldState, topBar = {
//        TopBar("学籍管理")
//    }) {
//    Row {
//        NavRail(navi, "/library")
//            Box(Modifier.fillMaxSize()) {
//                Box(Modifier.width(800.dp).align(Alignment.TopCenter)) {
    TeachingAffairsStatusForUser(viewModel)
//                }
//            }
//    }
//    }
}