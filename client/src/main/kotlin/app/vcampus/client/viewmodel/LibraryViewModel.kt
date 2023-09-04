package app.vcampus.client.viewmodel


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import app.vcampus.client.repository.FakeRepository
import app.vcampus.client.scene.components.SideBarItem
import app.vcampus.server.entity.LibraryBook
import app.vcampus.server.enums.BookStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LibraryViewModel : ViewModel() {
    val identity = FakeRepository.user.roles.toList()
    val addBook = AddBook()

    val sideBarContent = (if (identity.contains("library_user")) {
        listOf(SideBarItem(true, "查询", "", Icons.Default.Info, false))
    } else {
        emptyList()
    }) + identity.flatMap {
        when (it) {
            "library_user" -> listOf(
                    SideBarItem(false, "查询图书", "查找图书馆藏书",
                            Icons.Default.Search, false),
                    SideBarItem(false, "我的书籍", "查看已借阅书籍",
                            Icons.Default.MenuBook, false)
            )

            else -> emptyList()
        }
    } + (if (identity.contains("library_user")) {
        listOf(SideBarItem(true, "借还相关", "", Icons.Default.Info, false))
    } else {
        emptyList()
    }) + identity.flatMap {
        when (it) {
            "library_user" -> listOf(
                    SideBarItem(false, "预约还书", "预约还书时间",
                            Icons.Default.Event,
                            false)
            )

            else -> emptyList()
        }
    } + (if (identity.contains("library_staff")) {
        listOf(SideBarItem(true, "管理工具", "", Icons.Default.Info, false))
    } else {
        emptyList()
    }) + identity.flatMap {
        when (it) {
            "library_staff" -> listOf(
                    SideBarItem(false, "添加图书", "添加新的图书",
                            Icons.Default.LibraryAdd,
                            false),
                    SideBarItem(false, "修改图书", "修改现有图书",
                            Icons.Default.AutoFixHigh,
                            false),
                    SideBarItem(false, "办理借还书", "办理借还书业务",
                            Icons.Default.Queue,
                            false)
            )

            else -> emptyList()
        }
    }


    val librarySideBarItem = sideBarContent.toMutableStateList()

    class AddBook : ViewModel() {
        var isbn = mutableStateOf("")
        var showDetails = mutableStateOf(false)
        var name = mutableStateOf("")
        var author = mutableStateOf("")
        var press = mutableStateOf("")
        var description = mutableStateOf("")
        var place = mutableStateOf("")
        var cover = mutableStateOf("")
        var bookStatus = mutableStateOf(BookStatus.available)

        var showMessage = mutableStateOf(false)
        var result = mutableStateOf(true)

        fun preAddBook() {
            showDetails.value = false

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    preAddBookInternal().collect {
                        name.value = it.name ?: ""
                        author.value = it.author ?: ""
                        press.value = it.press ?: ""
                        description.value = it.description ?: ""
                        cover.value = it.cover ?: ""

                        showDetails.value = true
                    }
                }
            }
        }

        private suspend fun preAddBookInternal() = flow {
            emit(FakeRepository.preAddBook(isbn.value))
        }

        fun addBook() {
            showMessage.value = false

            val newBook = LibraryBook()

            newBook.isbn = isbn.value
            newBook.name = name.value
            newBook.author = author.value
            newBook.press = press.value
            newBook.description = description.value
            newBook.place = place.value
            newBook.bookStatus = bookStatus.value
            newBook.cover = cover.value

            if (name.value == "" || isbn.value == "" || author.value == "" ||
                    press.value == "" || description.value == "" || place.value == "") {
                showMessage.value = true
                result.value = false
                return
            }

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    addBookInternal(newBook).collect {
                        result.value = it
                        showMessage.value = true
                    }
                }
            }
        }

        private suspend fun addBookInternal(newBook: LibraryBook) = flow {
            emit(FakeRepository.addBook(newBook))
        }
    }
}
