// Kiểu thời gian (10.seconds)
import korlibs.time.*
// Giả lập input (simulateClick, onClick)
import korlibs.korge.input.*
// Lớp hỗ trợ test có View
import korlibs.korge.tests.*
// Hàm tween (chuyển động mượt)
import korlibs.korge.tween.*
// Các View (solidRect...)
import korlibs.korge.view.*
// Bảng màu
import korlibs.image.color.*
// Kiểu hình học (Rectangle)
import korlibs.math.geom.*
// Thư viện test
import kotlin.test.*

// Test mẫu của template KorGE: kiểm tra môi trường test có View hoạt động
class MyTest : ViewsForTesting() {
    @Test
    fun test() = viewsTest {
        // Danh sách ghi lại các lần click
        val log = arrayListOf<String>()
        // Tạo hình chữ nhật đỏ 100x100
        val rect = solidRect(100, 100, Colors.RED)
        // Khi click thì ghi vào log
        rect.onClick {
            log += "clicked"
        }
        // Stage có đúng 1 view con
        assertEquals(1, views.stage.numChildren)
        // Giả lập click
        rect.simulateClick()
        // Hình đang hiển thị trên màn hình
        assertEquals(true, rect.isVisibleToUser())
        // Di chuyển hình ra ngoài màn hình bên trái trong 10 giây
        tween(rect::x[-102], time = 10.seconds)
        // Vị trí cuối cùng đúng như mong đợi
        assertEquals(Rectangle(x=-102, y=0, width=100, height=100), rect.globalBounds)
        // Hình không còn hiển thị
        assertEquals(false, rect.isVisibleToUser())
        // Đã ghi nhận đúng một lần click
        assertEquals(listOf("clicked"), log)
    }
}
