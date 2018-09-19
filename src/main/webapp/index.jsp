<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<body>
<h2>Hello World!</h2>

springmvc上传文件
<form name="form1" action="/manage/product/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file" />
    <input type="submit" value="springmvc上传文件" />
</form>


富文本图片上传文件
<form name="form2" action="/manage/product/rich_text_img_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file" />
    <input type="submit" value="富文本图片上传文件" />
</form>
</body>
</html>
