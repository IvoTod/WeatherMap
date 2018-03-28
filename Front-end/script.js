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
        var uluru = {lat: -37.663712, lng: 144.844788, name: "Melbourne"};
        var marker2 = {lat: 36.114647, lng: -115.172813, name: "Las Vegas"};
        var marker3 = {lat: 52.520008, lng: 13.404954, name: "Berlin"};
        var marker4 = {lat: 51.509865, lng: -0.118092, name: "London"};
        var marker5 = {lat: 47.751076, lng: -120.740135, name: "Washington"};
        var marker6 = {lat: 55.751244, lng: 37.618423, name: "Moscow"};
        var marker7 = {lat: -29.851847, lng: 30.993368, name: "South Africa"};
        var locations = [];
        locations.push(uluru);
        locations.push(marker2);
        locations.push(marker3);
        locations.push(marker4);
        locations.push(marker5);
        locations.push(marker6);
        locations.push(marker7);
        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 3,
          center: uluru
        });
        for (var i = 0; i < locations.length; i++) {
            var marker = new google.maps.Marker({
                animation: google.maps.Animation.DROP,
                position: locations[i],
                map: map,
                id: i,
                name: locations[i].name 
            });
            google.maps.event.addListener(marker, 'click', function() { 
                $("#id span").text(this.id);
                $("#name span").text(this.name);
                $("#latitude span").text(this.position.lat);
                $("#longitude span").text(this.position.lng);
            });
        }
}

$(document).ready(function () {
    initMap();
});