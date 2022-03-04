package example.app_contacts_hw3

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.SystemClock.sleep
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    private lateinit var mDevice: UiDevice

    @get:Rule
    var permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)


    @Before
    fun appTurnOn() {
        mDevice = UiDevice.getInstance(getInstrumentation())

        mDevice.pressHome()

        val launcherPackage: String = getLauncherPackageName()

        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)

        val context: Context = getApplicationContext()
        val intent: Intent? =
            context.packageManager.getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances

        context.startActivity(intent)

        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), 5000)
    }

    @Test
    fun basicUseAppContext() {

        val appContext = getInstrumentation().targetContext
        assertEquals("example.app_contacts_hw3", appContext.packageName)

    }

    /**
     * 1) Найти предпоследний элемент в recyclerView и проскроллить до него
     * 2) Тыкнуть на нем кнопку 'Share'
     * 3) Тыкнуть кнопку 'Назад'
     * 4) Ввести в поиск номер "1377222" (Взял его, чтобы не попался реальный номер)
     * 5) Переворачиваем экран
     * 6) Если список пустй, то очищаем EditText
     * 7) Снова переворачиваем экран
     * 8) Тыкаем в первом элементе на кнопку 'Message'
     * 9) Тадам
     * */

    @Test
    fun shareContactTest() {
        val list = UiScrollable(UiSelector().resourceId(viewId("myRecyclerView")))

        if (list.childCount == 0) return


        val numberOfList = list.childCount - 1
        val uiObjectShareButton = list.getChild(getRowItem(numberOfList, "share"))


        list.scrollToEnd(999, 15)
        uiObjectShareButton.click()
        sleep(1500);
        mDevice.pressBack()
        val editText = mDevice.findObject(UiSelector().resourceId(viewId("edit_text")))
        editText.text = "1377222"
        sleep(1500)
        mDevice.setOrientationLeft()
        sleep(1500)

        val newList = UiScrollable(UiSelector().resourceId(viewId("myRecyclerView")))
        if (newList.childCount == 0) {
            sleep(1500)
            editText.text = ""
            sleep(1500)
        }

        assertNotEquals(0, newList.childCount)

        mDevice.setOrientationNatural()
        sleep(1500)

        val uiObjectMessageButton = list.getChild(getRowItem(0, "message"))
        assertNotNull(uiObjectMessageButton)
        uiObjectMessageButton.click()

    }


    private fun viewId(id: String): String {
        return "$BASIC_SAMPLE_PACKAGE:id/$id"
    }

    private fun getRowItem(itemIndex: Int, resourceId: String): UiSelector? {
        return UiSelector()
            .resourceId(viewId(resourceId))
            .enabled(true).instance(itemIndex)
    }


    private fun getLauncherPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo!!.activityInfo.packageName
    }
}