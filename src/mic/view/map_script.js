
/*
var iconFeature = new ol.Feature({
        geometry: new ol.geom.Point([137.4416334989196,-4.5894669521344875]),
        name: 'Null Island',
        population: 4000,
        rainfall: 500
      });
	  
var iconFeature2 = new ol.Feature({
        geometry: new ol.geom.Point([137.44330085314513,	-4.5902425318647895]),
        name: 'Null Island',
        population: 4000,
        rainfall: 500
      });

      var vectorSource = new ol.source.Vector({
        features: [iconFeature,iconFeature2]
      });

      var vectorLayer = new ol.layer.Vector({
        source: vectorSource
      });
*/
var mapExtent = [135.91879591, -5.99075187, 138.92007656, -2.98947123];
var mapMinZoom = 5;
var mapMaxZoom = 8;
var mapMaxResolution = 0.00274658;
var tileExtent = [-180.00000000, -90.00000000, 180.00000000, 90.00000000];
// Proj4js definition (verify code at http://epsg.io/4326);
// proj4.defs('EPSG:4326', '+proj=longlat +ellps=WGS84 +towgs84=0,0,0,0,0,0,0 +no_defs ');

var mapResolutions = [];
for (var z = 0; z <= mapMaxZoom; z++) {
  mapResolutions.push(Math.pow(2, mapMaxZoom - z) * mapMaxResolution);  
}

var mapTileGrid = new ol.tilegrid.TileGrid({
  extent: tileExtent,
  minZoom: mapMinZoom,
  resolutions: mapResolutions
});

var layer = new ol.layer.Tile({
  source: new ol.source.XYZ({
    projection: 'EPSG:4326',
    tileGrid: mapTileGrid,
    url: "{z}/{x}/{y}.png"
  })
});


var map = new ol.Map({
  target: 'map',
  layers: [layer],
  view: new ol.View({
    projection: ol.proj.get('EPSG:4326'),
    extent: mapExtent,
    maxResolution: mapTileGrid.getResolution(mapMinZoom)
  })
});
alert(map.getSize());
map.getView().fit(mapExtent, map.getSize());
map.addControl(new ol.control.MousePosition());
addMarker(137.4416334989196,-4.589466952134487);

function addMarker(longi, lati) {

               var iconFeature = new ol.Feature({
                    geometry: new ol.geom.Point([longi, lati]),
                    name: 'Null Island',
                    population: 4000,
                    rainfall: 500
                });

                var vectorSource = new ol.source.Vector({
					features: [iconFeature]
				});

				var vectorLayer = new ol.layer.Vector({
					source: vectorSource
				});
				
                map.addLayer(vectorLayer);
            


}