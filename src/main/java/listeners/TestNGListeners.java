package listeners;

import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.FileUtils;
import utils.LogsUtil;

import java.io.File;

public class TestNGListeners implements IExecutionListener, ITestListener {

    File allureResults = new File("Test-Outputs/allure-results");
    File logs = new File("Test-Outputs/Logs");
    File srcEnvironmentFile = new File("src/test/resources/Properties/environment.properties");
    File desEnvironmentFile = new File("Test-Outputs/allure-results/environment.properties");

    @Override
    public void onExecutionStart() {
        LogsUtil.info("Test execution started");
        FileUtils.deleteFiles(allureResults);
        FileUtils.cleanDirectory(logs);
        FileUtils.createDirectory(logs);
        FileUtils.createDirectory(allureResults);
        FileUtils.copyFile(srcEnvironmentFile, desEnvironmentFile);
    }

    @Override
    public void onExecutionFinish() {
        LogsUtil.info("Test execution finished");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogsUtil.info("Test case ", result.getName(), " passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogsUtil.info("Test case ", result.getName(), " failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogsUtil.info("Test case ", result.getName(), " skipped");
    }
}
