<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="panel white t50 b20">
    <div class="undercon" style="text-align:center;font-size:100px;font-weight:bold;color:#ccc;padding:50px 0;">
      <svg xmlns="http://www.w3.org/2000/svg"
        version="1.1"
        width="70%" viewBox="0 0 971 971"
        onload="init(evt)" style="max-width:300px;">

        <style>
            .gear {
              stroke: #4B555A;
              fill:	#707D83;
            }
        </style>

        <script type="text/ecmascript">
          var svgNS = "http://www.w3.org/2000/svg";

          function init(evt)
          {
            if (window.svgDocument == null)
            {
              svgDocument = evt.target.ownerDocument;
            }
            addRotateTransform('gear-1', 12, 1);
          }

          function addRotateTransform(target_id, speed, direction)
          {
            var element_to_rotate = svgDocument.getElementById(target_id);
            var my_transform = svgDocument.createElementNS(svgNS, "animateTransform");

            var bb = element_to_rotate.getBBox();
            var cx = bb.x + bb.width / 2;
            var cy = bb.y + bb.height / 2;

            my_transform.setAttributeNS(null, "attributeName", "transform");
            my_transform.setAttributeNS(null, "attributeType", "XML");
            my_transform.setAttributeNS(null, "type", "rotate");
            my_transform.setAttributeNS(null, "dur", speed + "s");
            my_transform.setAttributeNS(null, "repeatCount", "indefinite");
            my_transform.setAttributeNS(null, "from", 360 * direction + " " + cx + " " + cy);
            my_transform.setAttributeNS(null, "to", "0 " + cx + " " + cy);

            element_to_rotate.appendChild(my_transform);
            my_transform.beginElement();
          }
        </script>

        <g transform="translate(0,971) scale(0.1,-0.1)" stroke="none">
          <path d="M3750 9430 c-27 -87 -84 -259 -124 -382 -41 -123 -127 -385 -191
            -583 -64 -198 -175 -540 -247 -760 -71 -220 -159 -490 -195 -600 -36 -110 -73
            -225 -83 -255 -10 -30 -71 -217 -135 -415 -64 -198 -122 -378 -130 -400 -7
            -22 -66 -202 -130 -400 -65 -198 -161 -493 -215 -655 -152 -458 -293 -888
            -355 -1085 -31 -99 -68 -217 -83 -262 l-26 -83 -823 0 c-508 0 -823 -4 -823
            -9 0 -15 66 -230 90 -295 l21 -56 705 0 c387 0 704 -1 704 -3 0 -1 -23 -70
            -50 -152 -28 -83 -100 -305 -161 -495 -98 -308 -273 -842 -285 -875 -6 -15
            165 -198 330 -354 200 -188 513 -434 520 -409 2 7 25 74 49 148 68 204 429
            1302 573 1748 l128 392 1030 -2 1031 -3 52 -150 c28 -82 126 -368 218 -635 92
            -267 210 -609 262 -760 52 -151 124 -358 158 -460 35 -102 102 -295 148 -430
            47 -135 113 -328 147 -430 35 -102 65 -187 67 -189 1 -3 51 8 111 22 254 63
            538 161 789 272 128 56 131 58 123 84 -9 29 -100 295 -305 891 -75 217 -205
            598 -290 845 -85 248 -192 561 -239 698 l-85 247 1689 0 1689 0 21 56 c24 65
            90 280 90 295 0 5 -689 10 -1807 11 l-1808 3 -27 80 c-33 98 -232 678 -298
            870 -26 77 -92 266 -145 420 -53 154 -123 357 -155 450 -32 94 -104 303 -160
            465 -56 162 -128 372 -160 465 -32 94 -97 283 -145 420 -48 138 -91 261 -95
            275 -4 14 -40 117 -80 230 -39 113 -114 329 -165 480 -129 382 -184 542 -195
            565 -5 11 -53 146 -105 300 -53 154 -114 331 -136 393 -21 61 -57 165 -80 230
            -22 64 -58 167 -79 227 -21 61 -42 122 -45 138 -15 57 -34 28 -85 -133z m100
            -3285 c87 -247 119 -340 245 -705 75 -217 158 -456 185 -530 27 -74 78 -223
            115 -330 36 -107 130 -382 209 -610 l143 -415 -453 -3 c-250 -1 -658 -1 -908
            0 l-455 3 74 225 c84 259 324 994 426 1310 361 1109 377 1156 384 1148 2 -2
            18 -44 35 -93z" fill="#ccc">
            <animate dur="2s" values="#ccc; #4B555A; #ccc" keyTimes="0; 0.5; 1" attributeName="fill" repeatCount="indefinite"/>
          </path>
        </g>
        <g transform="translate(595, 432) scale(2,-2)">
          <path
            id="gear-1"
            class="gear"
            d="M 59.5 9.9 L 56.09375 10.5 L 54.78125 16.0625 L 54.875 18.90625 L 55.5 21.15625 L 49.96875 23.15625 L 49.03125 21.03125 L 47.28125 18.78125 L 42.6875 15.375 L 39.6875 17.125 L 40.34375 22.78125 L 41.40625 25.4375 L 42.78125 27.3125 L 38.28125 31.09375 L 36.65625 29.4375 L 34.21875 27.9375 L 28.78125 26.28125 L 26.53125 28.9375 L 29.09375 34.03125 L 31 36.15625 L 32.9375 37.46875 L 29.96875 42.5625 L 27.875 41.5625 L 25.09375 40.96875 L 19.40625 41.28125 L 18.21875 44.5625 L 22.375 48.46875 L 24.875 49.8125 L 27.125 50.375 L 26.125 56.1875 L 23.78125 55.9375 L 20.96875 56.34375 L 15.75 58.59375 L 15.75 62.0625 L 20.96875 64.34375 L 23.78125 64.71875 L 26.125 64.5 L 27.125 70.28125 L 24.875 70.875 L 22.375 72.1875 L 18.21875 76.125 L 19.40625 79.375 L 25.09375 79.71875 L 27.875 79.125 L 29.96875 78.09375 L 32.9375 83.21875 L 31 84.5 L 29.09375 86.625 L 26.53125 91.71875 L 28.78125 94.375 L 34.21875 92.75 L 36.65625 91.25 L 38.28125 89.5625 L 42.78125 93.375 L 41.40625 95.25 L 40.34375 97.875 L 39.6875 103.5625 L 42.6875 105.28125 L 47.28125 101.875 L 49.03125 99.65625 L 49.96875 97.53125 L 55.5 99.53125 L 54.875 101.78125 L 54.78125 104.625 L 56.09375 110.15625 L 59.5 110.75 L 62.65625 106 L 63.53125 103.28125 L 63.6875 100.96875 L 69.59375 100.96875 L 69.75 103.28125 L 70.625 106 L 73.78125 110.75 L 77.1875 110.15625 L 78.5 104.625 L 78.40625 101.78125 L 77.78125 99.53125 L 83.3125 97.53125 L 84.25 99.65625 L 86 101.875 L 90.59375 105.28125 L 93.59375 103.5625 L 92.9375 97.875 L 91.875 95.25 L 90.5 93.375 L 95.03125 89.5625 L 96.65625 91.25 L 99.0625 92.75 L 104.53125 94.375 L 106.75 91.71875 L 104.1875 86.625 L 102.28125 84.5 L 100.375 83.21875 L 103.3125 78.09375 L 105.40625 79.125 L 108.1875 79.71875 L 113.875 79.375 L 115.0625 76.125 L 110.90625 72.1875 L 108.40625 70.875 L 106.15625 70.28125 L 107.1875 64.5 L 109.5 64.71875 L 112.3125 64.34375 L 117.53125 62.0625 L 117.53125 58.59375 L 112.3125 56.34375 L 109.5 55.9375 L 107.1875 56.1875 L 106.15625 50.375 L 108.40625 49.8125 L 110.90625 48.46875 L 115.0625 44.5625 L 113.875 41.28125 L 108.1875 40.96875 L 105.40625 41.5625 L 103.3125 42.5625 L 100.375 37.46875 L 102.28125 36.15625 L 104.1875 34.03125 L 106.75 28.9375 L 104.53125 26.28125 L 99.0625 27.9375 L 96.65625 29.4375 L 95.03125 31.09375 L 90.5 27.3125 L 91.875 25.4375 L 92.9375 22.78125 L 93.59375 17.125 L 90.59375 15.375 L 86 18.78125 L 84.25 21.03125 L 83.3125 23.15625 L 77.78125 21.15625 L 78.40625 18.90625 L 78.5 16.0625 L 77.1875 10.5 L 73.78125 9.90625 L 70.625 14.6875 L 69.75 17.375 L 69.59375 19.6875 L 63.6875 19.6875 L 63.53125 17.375 L 62.65625 14.6875 L 59.5 9.90625 z M 63.15625 26.53125 L 63.15625 47.28125 C 58.497767 48.520845 54.833345 52.185267 53.59375 56.84375 L 32.84375 56.84375 C 34.480897 40.866664 47.179164 28.168397 63.15625 26.53125 z M 70.15625 26.53125 C 86.133336 28.168397 98.831603 40.866664 100.46875 56.84375 L 79.6875 56.84375 C 78.451207 52.197675 74.797055 48.561686 70.15625 47.3125 L 70.15625 26.53125 z M 66.625 53.8125 C 70.214851 53.8125 73.125 56.722649 73.125 60.3125 C 73.125 63.902351 70.214851 66.8125 66.625 66.8125 C 63.035149 66.8125 60.125 63.902351 60.125 60.3125 C 60.125 56.722649 63.035149 53.8125 66.625 53.8125 z M 32.84375 63.84375 L 53.625 63.84375 C 54.874186 68.484555 58.510175 72.138707 63.15625 73.375 L 63.15625 94.15625 C 47.179164 92.519103 34.480897 79.820836 32.84375 63.84375 z M 79.65625 63.84375 L 100.46875 63.84375 C 98.831603 79.820836 86.133336 92.519103 70.15625 94.15625 L 70.15625 73.34375 C 74.784661 72.0979 78.4104 68.472161 79.65625 63.84375 z"
          />
        </g>
      </svg>
      <p>Hello, World!</p>
    </div>
  </div>