from selenium import webdriver
#from selenium.webdriver.common.keys import Keys

driver = webdriver.Chrome(executable_path = r"C:\Users\shant\eclipse-workspace\PythonSelenium\chromedriver.exe")
driver.get("http://www.python.org")
assert "Python" in driver.title
elem = driver.find_element_by_name("q")
print(driver.find_element_by_id("about").text)
driver.close()