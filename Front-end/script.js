function showTemperature() {
    $("#pollution-button").removeClass("clicked");
    $("#temperature-button").addClass("clicked");
    $(".pollution").hide();
    $(".temperature").show();
}

function showPollution() {
    $("#temperature-button").removeClass("clicked");
    $("#pollution-button").addClass("clicked");
    $(".temperature").hide();
    $(".pollution").show();
}

function initMap() {
    var map = new google.maps.Map(document.getElementById('map'), {
      zoom: 3,
      center: {lat: 0, lng: 0}
    });
    var locations = [];
    $.ajax({type:"GET", 
	    crossDomain: true, 
	    url:"http://localhost:8080/WeatherMap/url/Get/all", 
	    contentType: "application/x-www-form-urlencoded; charset=utf-8", 
	    success: function( locations ) {
		    for (var i = 0; i < locations.length; i++) {
			    var marker = new google.maps.Marker({
				    animation: google.maps.Animation.DROP,
				    position: {'lat': locations[i].latitude, 'lng': locations[i].longitude},
				    map: map,
				    id: locations[i].id,
				    name: locations[i].name 
			    });
			    google.maps.event.addListener(marker, 'click', function(test) { 
				    $("#name").text(this.name);
				    $(".field").text("");
				    $.ajax({type:"GET", 
					    crossDomain: true, 
					    url:"http://localhost:8080/WeatherMap/url/Get/station?id=" + this.id, 
					    contentType: "application/x-www-form-urlencoded; charset=utf-8", 
					    success: function( data ) {
						    console.log(data);
						    $(".temperatureCurrent span").text(data.temperature);
						    $(".humidityCurrent span").text(data.humidity);
						    $(".apmCurrent span").text(data.apm);
						    $(".airPressureCurrent span").text(data.airPressure);
					    },
					    error: function() {
						    console.log("error");
					    }
				    });
				    $.ajax({
					type: 'GET',
					url: "http://localhost:8080/WeatherMap/url/Get/average?id=" + this.id,
					success: function (data) {
					    $("#2h,#5h,#12h").hide();
					    console.log(data);
					    if(data["2hr"]) {
						$("#2h").show();
						$(".airPressure2H span").text(data["2hr"].airPressure);
						$(".temperature2H span").text(data["2hr"].temperature);
						$(".humidity2H span").text(data["2hr"].humidity);
						$(".apm2H span").text(data["2hr"].apm);
					    }
					    if(data["5hr"]) {
						$("#5h").show();
						$(".airPressure5H span").text(data["5hr"].airPressure);
						$(".temperature5H span").text(data["5hr"].temperature);
						$(".humidity5H span").text(data["5hr"].humidity);
						$(".apm5H span").text(data["5hr"].apm);
					    }
					    if(data["12hr"]) {
						$("#12h").show();
						$(".airPressure12H span").text(data["12hr"].airPressure);
						$(".temperature12H span").text(data["12hr"].temperature);
						$(".humidity12H span").text(data["12hr"].humidity);
						$(".apm12H span").text(data["12hr"].apm);
					    }
					}
				    });
			    });
		    }
	    },
	    error: function() {
		    console.log("error");
	    }
    });
    //for (var i = 0; i < locations.length; i++) {
	//var marker = new google.maps.Marker({
	    //animation: google.maps.Animation.DROP,
	    //position: locations[i],
	    //map: map,
	    //id: i,
	    //name: locations[i].name 
	//});
	//google.maps.event.addListener(marker, 'click', function() {
	//});
    //}
}
$(document).ready(function() {
    showTemperature();
});
