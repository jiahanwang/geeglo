// JavaScript Document
$(function(){
	var pattern = /\s*\S+/;
	// request geegle
	function sendQuery(query_input) {
			if(!pattern.test(query_input)) return;
			var query = query_input.trim();
			// hide #content_wrapper_homepage and #footer_homepage
			$("#content_wrapper_homepage").css("display", "none");
			$("#footer_homepage").css("display", "none");
			// show and change the value of #header_wrapper
			$("#search_form_input").val(query);
			$("#header_wrapper").animate({top: "0px"}, 300);
			// show the loading picture
			$("#content_wrapper").css("display", "block");
			$("#content_wrapper .loading").fadeIn("fast");
			$("#content_wrapper .error_info").fadeOut("fast");
			$("#content").fadeOut("fast");
			$("#content_wrapper .links_wrapper").empty();
			// send the query
			var data = {};
			data.query = query;
			$.get("/com.geeglo/QueryServlet", data, function(returnData){
				var data  = JSON.parse(returnData);
				if(data.success == false){
					// show no results for the one 
					$("#content_wrapper .loading").css("display", "none");
					$("#content_wrapper .error_info h1").text("Server error.");
					$("#content_wrapper .error_server_error").css("display", "block");
					$("#content_wrapper .error_no_files").css("display", "none");
					$("#content_wrapper .error_info").fadeIn("fast");
					return;
				}
				if(!data.documents){
					// show no results for the one
					$("#content_wrapper .loading").css("display", "none");
					$("#content_wrapper .error_info h1").text("Your search - " + data.terms.join(" ") + " - has no matching documents.");
					$("#content_wrapper .error_no_files").css("display", "block");
					$("#content_wrapper .error_server_error").css("display", "none");
					$("#content_wrapper .error_info").fadeIn("fast");
					return;
				}
				// build the result
				var documents  = data.documents;
				var pattern = new RegExp(data.terms.join("|"), "gi");
				function highlight(match){
					return "<b>" + match + "</b>";
				}
				$("#content .result_info p").text("Retrieved in " + data.process_time/1000 + " seconds");
				console.log("Geegle: ");
				for(var i = 0; i < documents.length; i++){
					var $div = $("<div class='links_main links_deep'>");
					// title 
					var $title = $("<a class='large'>");
					if(documents[i].title == "")
						$title.html("[Untitled Document]");
					else
						$title.html(unescape(documents[i].title.replace(pattern, highlight)));
					$title.attr("href", documents[i].url);
					$div.append($("<h2>").append($title));
					// snippet
					var $snippet = $("<div class='snippet'>");
					$snippet.html(unescape(documents[i].snippet.replace(pattern, highlight)));
					$div.append($snippet);
					// url
					var $url = $("<a class='url'>");
					$url.html(documents[i].url.replace(pattern, highlight));
					$url.attr("href", documents[i].url);
					$div.append($("<div>").append($url));
					$("#content .links_wrapper").append($("<div class=results_links_deep highlight_d2'>").append($div));
					//
					console.log(documents[i].url);
				}
				$("#content").fadeIn("fast");
				$("#content_wrapper .loading").css("display", "none");
			}).error(function (){
				$("#content_wrapper .loading").css("display", "none");
				$("#content_wrapper .error_info h1").text("Server error.");
				$("#content_wrapper .error_server_error").css("display", "block");
				$("#content_wrapper .error_no_files").css("display", "none");
				$("#content_wrapper .error_info").fadeIn("fast");
			});
	}
	// request Google
	function requestGoogle(query_input) {
		if(!pattern.test(query_input)) return;
		var query = query_input.trim();
		// show the loading picture
		$("#content_wrapper_google").css("display", "block");
		$("#content_wrapper_google .loading").fadeIn("fast");
		$("#content_wrapper_google .error_info").fadeOut("fast");
		$("#content_google").fadeOut("fast");
		$("#content_wrapper_google .links_wrapper").empty();
		// send the query
		var url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyA73mVdZIyR-bzSVXjw1gKNZTc3CSGAnE0&cx=017882124825919339588:ylgoivthn8u&q=site:ics.uci.edu " + query;		
		$.ajax({
		    url: encodeURI(url),
		    // the name of the callback parameter, as specified by the YQL service
		    jsonp: "callback",
		    // tell jQuery we're expecting JSONP
		    dataType: "jsonp",
		    // work with the response
		    success: function( response ) {
		        console.log( response ); // server response
				if(!response.items){
					// show no results for the one
					$("#content_wrapper_google .loading").css("display", "none");
					$("#content_wrapper_google .error_info h1").text("Your search - " + query + " - has no matching documents.");
					$("#content_wrapper_google .error_no_files").css("display", "block");
					$("#content_wrapper_google .error_server_error").css("display", "none");
					$("#content_wrapper_google .error_info").fadeIn("fast");
					return;
				}
				// build the result
				var documents  = response.items;
				$("#content_google .result_info p").text("Retrieved in " + response.searchInformation.searchTime + " seconds");
				console.log("Google: ");
				for(var i = 0; i < documents.length; i++){
					var $div = $("<div class='links_main links_deep'>");
					// title 
					var $title = $("<a class='large'>");
					if(documents[i].htmlTitle == "")
						$title.html("[Untitled Document]");
					else
						$title.html(documents[i].htmlTitle);
					$title.attr("href", documents[i].link);
					$div.append($("<h2>").append($title));
					// snippet
					var $snippet = $("<div class='snippet'>");
					$snippet.html(documents[i].htmlSnippet);
					$div.append($snippet);
					// url
					var $url = $("<a class='url'>");
					$url.html(documents[i].htmlFormattedUrl);
					$url.attr("href", documents[i].link);
					$div.append($("<div>").append($url));
					$("#content_google .links_wrapper").append($("<div class=results_links_deep highlight_d2'>").append($div));	
					//
					console.log(documents[i].link);
				}
				$("#content_google").fadeIn("fast");
				$("#content_wrapper_google .loading").css("display", "none");
		        
		    },
		    error: function(){
				$("#content_wrapper .loading").css("display", "none");
				$("#content_wrapper .error_info h1").text("Server error.");
				$("#content_wrapper .error_server_error").css("display", "block");
				$("#content_wrapper .error_no_files").css("display", "none");
				$("#content_wrapper .error_info").fadeIn("fast");
		    }
		});
	}
	// home page search box
	$("#search_form_homepage").submit(function (event){ 
		var query = $("#search_form_input_homepage").val();
		sendQuery(query);
		requestGoogle(query);
		event.preventDefault();
	});	
	
	$("#search_form_homepage .search_logo").click(function (event){ 
		$("#search_form_input_homepage").submit();
	});	
	
	// result page search box
	$("#search_form").submit(function (event){ 
		var query = $("#search_form_input").val();
		sendQuery(query);
		requestGoogle(query);
		event.preventDefault();
	});	
	
	$("#search_form .search_logo").click(function (event){ 
		$("#search_form_input").submit();
	});	
	
	// compare to Google
	$("#compare_button").click(function(event){
		if($("#content_wrapper_google").hasClass("close")){
			$("#content_wrapper_google").animate({right: "0px"}, 250);
			$("#content_wrapper").animate({width: "50%"}, 250);
			$("#content_wrapper_google").removeClass("close");
			
		}else{
			$("#content_wrapper_google").animate({right: "-50%"}, 250);
			$("#content_wrapper").animate({width: "100%"}, 250);
			$("#content_wrapper_google").addClass("close");
		}
	});
	
	
	
});