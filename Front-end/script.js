// function openCity(evt, cityName) {
//     // Declare all variables
//     var i, tabcontent, tablinks;

//     // Get all elements with class="tabcontent" and hide them
//     tabcontent = document.getElementsByClassName("tabcontent");
//     for (i = 0; i < tabcontent.length; i++) {
//         tabcontent[i].style.display = "none";
//     }

//     // Get all elements with class="tablinks" and remove the class "active"
//     tablinks = document.getElementsByClassName("tablinks");
//     for (i = 0; i < tablinks.length; i++) {
//         tablinks[i].className = tablinks[i].className.replace(" active", "");
//     }

//     // Show the current tab, and add an "active" class to the button that opened the tab
//     document.getElementById(cityName).style.display = "block";
//     evt.currentTarget.className += " active";
// }

function initMap() {
	locations = [];
        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 3,
          center: {lat: 0, lng:0}
        });
	$.ajax({type:"GET", 
		crossDomain: true, 
		url:"http://87.120.33.83:8080/WeatherMap/url/Get/all", 
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
					$.ajax({type:"GET", 
						crossDomain: true, 
						url:"http://87.120.33.83:8080/WeatherMap/url/Get/station?id=" + this.id, 
						contentType: "application/x-www-form-urlencoded; charset=utf-8", 
						success: function( data ) {
							$("#temperature span").text(data.temperature);
							$("#humidity span").text(data.humidity);
							$("#apm span").text(data.apm);
							$("#airPressure span").text(data.airPressure);
						},
						error: function() {
							console.log("error");
						}
					});
				});
			}
		},
		error: function() {
			console.log("error");
		}
	});
}

$(document).ready(function () {
    initMap();
});
