<%@page import="main.*"%>
<%@ page import="java.util.Random,java.text.*"%>
<%@ page import="marytts.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cocktail CAPTCHA Generator</title>
</head>
<body>
 
    <%
        CaptchaCreator c = new CaptchaCreator();
    	c.digitCaptchaGen();
    %>
    
	
	<input type="file" id="input"/>	
	<script type="text/javascript">	
		input.onchange = function(e){
		  var sound = document.getElementById('sound');
		  sound.src = URL.createObjectURL(this.files[0]);
		  sound.onend = function(e) {
		    URL.revokeObjectURL(this.src);
		  }
		}
	</script>
	<audio id="sound" controls></audio>
	
	<br><br>
	
	<%out.print("Type in the digits that the " + c.listenFor + " is saying."); %>
	
	<br><br>
	
	<form name = "myForm">
  <div>
    <label for="ans">Answer: </label>
    <input type="text" id="ans" name="answer"
           placeholder="Digits, no spaces">
  </div>
  <div>
    <button onclick=validate()>Submit</button>
  </div>
</form>

<script type="text/javascript">
	function validate() {
    	<%String ans = c.getAnswer();%>
		var x = "<%=ans%>";
		if (x == document.forms["myForm"]["answer"].value) {
			document.write("Correct - answer is " + x,"<br><br>",
					"<a href='<%= request.getRequestURI() %>'>Click to generate new CAPTCHA</a>");
		}
		else {
			document.write("Incorrect - answer is " + x,"<br><br>",
					"<a href='<%= request.getRequestURI() %>'>Click to generate new CAPTCHA</a>");

		}
	}
	
</script>

<br>
 
 	<br>
    <a href="<%= request.getRequestURI() %>">Click to generate new CAPTCHA</a>
     
</body>
</html>