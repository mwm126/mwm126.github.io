module Ahu exposing (..)

import Char exposing (..)
import Color exposing (..)
import Html exposing (Html, div, button, text, label)
import Html.Attributes exposing (style, placeholder)
import Html.Events exposing (onClick)
import Svg exposing (..)
import Svg.Attributes exposing (..)
import Time exposing (Time, second)

main =
  Html.program { init = init
               , update = update
               , subscriptions = subscriptions
               , view = view
               }


type alias Model = { sa_t : Float
                   , oa_p : Float
                   , cfm : Float
                   , oa_t : Float
                   , oa_wb : Float
                   , tons : Float
                   , shf : Float
                   , cycle : Int
                   , time : Float
                   , room_t : Float
                   , room_h : Float
                   }

init : (Model, Cmd Msg)
init = (
        { sa_t = 62
        , oa_p = 50
        , cfm = 30000
        , oa_t = 90
        , oa_wb = 84
        , tons = 65
        , shf = 0.90
        , cycle = 5
        , time = 0
        , room_t = 60
        , room_h = 0.015
        }
       , Cmd.none)

type Msg = IncrementOap (Model->Float) Float
         | IncrementSat (Model->Float) Float
         | IncrementCfm (Model->Float) Float
         | IncrementOat (Model->Float) Float
         | IncrementOawb (Model->Float) Float
         | IncrementTons (Model->Float) Float
         | IncrementShf (Model->Float) Float
         | IncrementCycle (Model->Int) Int
         | Tick Time

cycle_time = 6
time_mod : Time -> Model -> Float
time_mod time model =
    let
        t = Time.inSeconds time
        ct = toFloat model.cycle
    in
        (t - ct*(toFloat (floor(t/ct))))/ct

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    let
        new_model = case msg of
                        IncrementOap f dd -> { model | oa_p = (Basics.max 20.0 (Basics.min 100.0 (f model + dd)))}
                        IncrementSat f dd -> { model | sa_t = (Basics.max 42.0 (Basics.min 120.0 (f model + dd)))}
                        IncrementCfm f dd -> { model | cfm = (Basics.max 20000.0 (Basics.min 40000.0 (f model + dd)))}
                        IncrementOat f dd -> { model | oa_t = (Basics.max 65.0 (Basics.min 94.0 (f model + dd)))}
                        IncrementOawb f dd -> { model | oa_wb = (Basics.max 65.0 (Basics.min 94.0 (f model + dd)))}
                        IncrementTons f dd -> { model | tons = (Basics.max 40.0 (Basics.min 100.0 (f model + dd)))}
                        IncrementShf f dd -> { model | shf = (Basics.max 0.5 (Basics.min 1.0 (f model + dd)))}
                        IncrementCycle f dd -> { model | cycle = f model + dd}
                        Tick newTime -> { model | time = time_mod newTime model}
    in
        (new_model, Cmd.none)

subscriptions : Model -> Sub Msg
subscriptions model = Time.every (0.1 * second) Tick

view : Model -> Html Msg
view model =
    let
        pro_x = 150.0
        pro_y = 30
    in
  div []
      [ Html.text "Adjust system"
      , div [blueStyle]
          [ control IncrementOap .oa_p 5 "OA %" model
          , control IncrementSat .sa_t 1 "SA T" model
          , control IncrementCfm .cfm 1000 "CFM" model
          ]
      , Html.text "Adjust weather"
      , div [redStyle]
          [ control IncrementOat .oa_t 1 "OA T" model
          , control IncrementOawb .oa_wb 3 "OA WB" model
          ]
      , Html.text "Adjust load"
      , div [grayStyle]
          [ control IncrementTons .tons 5 "Tons" model
          , control IncrementShf .time 0.05 "SHF" model
          , control IncrementCycle .cycle 1 "sim cycle (seconds)" model
          ]
      , Html.text (room_comment model.room_t model.room_h)
      , Html.p [] []
      , svg [viewBox "0 0 600 400", Svg.Attributes.width "600px" ]
            (List.concat [ house model
                         , (protractor pro_x pro_y 20 30 0.2 60 0.3)
                         , psych_chart model])
      ]

-- for drawing the house
air_radius = 10
duct_height = 3*air_radius
duct_width = 8*air_radius
-- house offsets
xx = 10
yy = 10
roof_width = 10
roof_height = 10

house model =
    let
       rr = air_radius
       hh = duct_height
       ww = duct_width
       rw = roof_width
       rh = roof_height
       (ax, ay) = .air_location (sprite_states model)
                   -- points in house icon
       xs = [ 0, rw, 2*rw/3, 2*rw/3, -2*rw/3, -2*rw/3, -rw ]
       ys = [ 0, rh, rh,       2*rh,    2*rh, rh,       rh ]
       house_points = List.map2 (\ x y -> toString (xx+ww+rr+x) ++ "," ++ toString (yy+hh/3+y)) xs ys
       coil_x = xx+ww*0.6
       coil_y = yy+hh
       coil x = line [ x1 (toString (coil_x+x)), y1 (toString (coil_y-rr)), x2 (toString (coil_x+x)), y2 (toString coil_y), stroke "blue" ] []
    in
        [ line [ x1 (toString xx), y1 (toString yy), x2 (toString (xx+ww)), y2 (toString yy), stroke "black" ] []
        , rect [ Svg.Attributes.x (toString xx), Svg.Attributes.y (toString (yy + rr)), width (toString (2*rr)), height (toString (rr)), fill "gray" ] [ ]
        , rect [ Svg.Attributes.x (toString (xx+3*rr)), Svg.Attributes.y (toString (yy + rr)), width (toString (ww-(xx+3*rr))), height (toString (rr)), fill "gray" ] [ ]
        , line [ x1 (toString xx), y1 (toString (yy+hh)), x2 (toString (xx+ww)), y2 (toString (yy+hh)), stroke "black" ] []
        , polygon [ points (String.concat (List.intersperse " " house_points)), stroke "black" ] []
        , coil 10
        , coil 12
        , coil 14
        , coil 16
        , coil 18
        , Svg.text_ [ x (toString coil_x), y (toString coil_y), dx "15", dy "15", fontSize "10", stroke "blue" ] [ Html.text "coil" ]
        , circle [ cx (toString ax), cy (toString ay), r "10", fill (sprite_states model).air_color ] [ ]
        ]

protractor : Float -> Float -> Float -> Float -> Float -> Float -> Float -> List ( Svg msg)
protractor t u w q shf q_in shf_in =
    let
        x_1 = t
        y_1 = u
        x_2 = t - q*sin(shf*pi/2)
        y_2 = u + q*cos(shf*pi/2)
        x_3 = t - q_in*sin(shf_in*pi/2)
        y_3 = u + q_in*cos(shf_in*pi/2)
    in
        [
            -- room center
        Svg.text_ [ x (toString x_1), y (toString y_1), dx "5", dy "-5", fontSize "10"   ] [ Html.text "Cooling Vectors (BTU/Hr)" ]
        , circle [ cx (toString x_1), cy (toString y_1), r "4", fill "green" ] [ ]
        , Svg.text_ [ x (toString x_1), y (toString y_1), dx "5", dy "5", fontSize "10"   ] [ Html.text "room" ]
            -- load vector
        , line [ x1 (toString x_1), y1 (toString y_1), x2 (toString x_2), y2 (toString y_2), stroke "black" ] []
        , circle [ cx (toString x_2), cy (toString y_2), r "4", fill "gray" ] [ ]
        , Svg.text_ [ x (toString x_2), y (toString y_2), dx "5", dy "5", fontSize "10"   ] [ Html.text "load" ]
            -- supply vector
        , line [ x1 (toString x_1), y1 (toString y_1), x2 (toString x_3), y2 (toString y_3), stroke "black" ] []
        , circle [ cx (toString x_3), cy (toString y_3), r "4", fill "blue" ] [ ]
        , Svg.text_ [ x (toString x_3), y (toString y_3), dx "5", dy "5", fontSize "10"   ] [ Html.text "supply" ]
        ]
saturation_line : List (Float, Float)
saturation_line = [ (40.0, 0.0052)
                  , (45.0, 0.0063)
                  , (50.0, 0.0076)
                  , (55.0, 0.0092)
                  , (60.0, 0.0112)
                  , (65.0, 0.0132)
                  , (70.0, 0.0158)
                  , (75.0, 0.0188)
                  , (80.0, 0.0223)
                  , (85.0, 0.0264)
                  , (90.0, 0.029)
                  , (95.0, 0.029)
                  ]

th_to_xy (t,h) = ((t - 40)*(toFloat bottom-100)/(95-40) + 100, (0.029-h)*(toFloat bottom-100)/(0.029-0.0052) + 100)

air_state : (Float,Float) -> String -> String -> String -> String -> List (Svg msg)
air_state th clr label d_x d_y =
    let
        (t, h) = th
        (x_1, y_1) = th_to_xy (t,h)
    in
        [ circle [ cx (toString x_1), cy (toString y_1), r "4", fill clr ] [ ]
        , Svg.text_ [ x (toString x_1), y (toString y_1), dx d_x, dy d_y, fontSize "10" ] [ Html.text label ]
        ]

avg : (Float,Float) -> (Float,Float) -> Float -> (Float,Float)
avg xy1 xy2 t =
    let
        (x1, y1) = xy1
        (x2, y2) = xy2
    in
        (x1 + (x2-x1)*t, y1 + (y2-y1)*t)

avg_int : Float -> Float -> Float -> String
avg_int r1 r2 t = toRadix 16 (round (r1 + (r2-r1)*t))

avg_color : Color -> Color -> Float -> String
avg_color c1 c2 t =
    let
        r1 = toFloat (Color.toRgb c1).red
        g1 = toFloat (Color.toRgb c1).green
        b1 = toFloat (Color.toRgb c1).blue
        r2 = toFloat (Color.toRgb c2).red
        g2 = toFloat (Color.toRgb c2).green
        b2 = toFloat (Color.toRgb c2).blue
    in
        "#" ++ avg_int r1 r2 t ++ avg_int g1 g2 t ++ avg_int b1 b2 t

mixed_th model = (70, 0.02)
room_th model = (model.room_t, model.room_h)

sa_th model = (model.sa_t, model.cfm / 10000000 + 0.005)

outside_th model = (model.oa_t, model.oa_wb / 10000 + 0.015)

type alias Sprites = { recirc_th : (Float,Float)
                     , oa_th : (Float,Float)
                     , air_location : (Float,Float)
                     , air_color : String
                     }
sprite_states : Model -> Sprites
sprite_states model =
     if model.time < 0.25 then
         let
             pp = ((model.time)/0.25)
         in
             { recirc_th = room_th model
             , oa_th = room_th model
             , air_location = avg (xx+duct_width, yy) (xx, yy) pp
             , air_color = avg_color green green pp
             }
     else if model.time < 0.5 then
              let
                  pp = ((model.time-0.25)/0.25)
              in
                  { recirc_th = avg (room_th model) (mixed_th model) pp
                  , oa_th = avg (outside_th model) (mixed_th model) pp
                  , air_location = avg (xx, yy) (xx, yy+duct_height) pp
                  , air_color = avg_color blue green pp
                  }

     else if model.time < 0.75 then
              let
                  pp = ((model.time-0.5)/0.25)
              in
                  { recirc_th = avg (mixed_th model) (sa_th model) pp
                  , oa_th = avg (mixed_th model) (sa_th model) pp
                  , air_location = avg (xx, yy+duct_height) (xx+duct_width, yy+duct_height) pp
                  , air_color = avg_color yellow blue pp
              }
     else
              let
                  pp = ((model.time-0.75)/0.25)
              in
                  { recirc_th = avg (sa_th model) (room_th model) pp
                  , oa_th = avg (sa_th model) (room_th model) pp
                  , air_location = avg (xx+duct_width, yy+duct_height) (xx+duct_width, yy) pp
                  , air_color = avg_color blue green pp
                  }

bottom = 400

psych_chart : Model -> List (Svg msg)
psych_chart model =
    let
        p_horiz (x,y) = line [ x1 (toString x), y1 (toString y), x2 (toString 1000), y2 (toString y), stroke "black" ] []
        p_vert (x,y) = line [ x1 (toString x), y1 (toString y), x2 (toString x), y2 (toString 1000), stroke "black" ] []
    in
        List.concat [ List.map p_horiz (List.map th_to_xy saturation_line)
                    , List.map p_vert  (List.map th_to_xy saturation_line)
                    , List.concat [ air_state (outside_th model) "red" "outside" "5" "5"
                                  , air_state (mixed_th model) "yellow" "mixed" "5" "5"
                                  , air_state (room_th model) "green" "room" "5" "5"
                                  , air_state (sa_th model) "blue" "supply" "5" "5"
                                  , air_state (.oa_th (sprite_states model)) "black" "OA" "15" "15"
                                  , air_state (.recirc_th (sprite_states model)) "black" "recirc" "15" "-5"
                                  ]
                    ]

control inc get diff label model = div []
                             [ button [ onClick (inc get -diff) ] [ Html.text "-" ]
                             , div [inlineStyle] [ Html.text (label ++ " " ++ (toString (get model))) ]
                             , button [ onClick (inc get diff) ] [ Html.text "+" ]
                             ]

inlineStyle = Html.Attributes.style
        [ ( "display", "inline" )
        ]

blueStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "background-color", "#9999FF" )
        ]

redStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "background-color", "#FF9999" )
        ]

grayStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "background-color", "#999999" )
        ]

-- p = 14.696  -- barometric pressure in psia
-- cp = 1000      -- specific heat of the room
-- room_T = 80      -- temperature in Farenheit
-- supply_T = 56      -- temperature in Farenheit
-- mixed_T = 83      -- temperature in Farenheit
-- outside_T = 90      -- temperature in Farenheit
-- room_rh = 50      -- relative humidity, percentage
-- supply_rh = 95      -- relative humidity, percentage
-- mixed_rh = 47      -- relative humidity, percentage
-- twb = 84      -- outside wet bulb temperature {related to outside relative humidity}
-- room_w = 0.011      -- pounds water vapor per pound dry air
-- supply_w = 0.0095      -- pounds water vapor per pound dry air
-- mixed_w = 0.0152      -- pounds water vapor per pound dry air
-- outside_w = 0.024      -- pounds water vapor per pound dry air
-- room_h = 31.2      -- enthalpy in BTUs per pound
-- supply_h = 23.7      -- enthalpy in BTUs per pound
-- mixed_h = 36.3      -- enthalpy in BTUs per pound
-- outside_h = 48.2      -- enthalpy in BTUs per pound
-- oa_percent = 30      -- percent of outside air
-- sa_cfm = 30000      -- supply air flow rate
-- q = 80      -- load in tons
-- shf = 0.9      -- sensible heat factor [dimensionless]

findSatVaporPressure farenheit_temperature =
-- convert to Rankine
    let t = farenheit_temperature + 459.67
    in
        -- sat vapor pressure
        e^(-10440.4/t - 11.29465
          - 0.027022355*t
              + 1.289036e-5*t*t
                  - 2.4780681e-9*t*t*t+6.5459673*logBase e t)

findEnthalpy w t = ((0.24+0.444*w)*t + 1061*w)

findHumidity pw p = 0.62198*(pw/(p - pw))

pw humidity temperature = (humidity/100)*(findSatVaporPressure temperature)

cool_supply sa_cfm room_t supply_t = sa_cfm*60*0.241*(room_t - supply_t)/(13.2*12000)

-- q_in = sa_cfm*60*(findEnthalpy room_w room_t - findEnthalpy room_w room_t)/(13.2*12000)
-- shf_in = cool_supply/q_in

room_t old_room_t q shf cool_supply cp = old_room_t + (q*shf - cool_supply)/cp

room_w pw p = findHumidity pw p
room_h room_w room_t = findEnthalpy room_w room_t

room_rh old_room_rh q shf room_w supply_w sa_cfm supply_t = old_room_rh +
    (q*(1-shf) - (room_w - supply_w)*sa_cfm*60*(1093 + 0.444*supply_t)/(13.2*12000))/100

room_comment room_rh room_t =
    if room_rh>60 then
        "Ugh!  It's too humid."
    else if room_rh<30 then
             "It's too dry."
         else if room_t>80 then
                  "Whew!  It's too hot in here!"
              else if room_t<70 then
                       "Brrr!  It's too cold in here!"
                   else
                       ""


-- updateOutsideConditions
    -- //double pws = findSatVaporPressure(outside_T);
-- pw_star = findSatVaporPressure twb
-- w_star = findHumidity pw_star p
-- outside_w ot = ((1093 - 0.556*twb)*w_star - 0.24*(ot - twb))/(1093 + 0.444*ot - twb)
-- outside_h ow ot = (findEnthalpy ow ot)

-- updateMixedConditions
-- mixed_t ot rt = (oa_percent/100)*ot + (1-oa_percent/100)*rt
-- mixed_w ow rw = (oa_percent/100)*ow + (1-oa_percent/100)*ow
-- pws mixed_t = findSatVaporPressure mixed_T
mixed_h mw mt = findEnthalpy mw mt

-- supply_rh supply_t = if supply_t<60 then
--                           95
--                       else
--                           95 - (supply_T - 60)*3

-- pw supply_rh supply_t = (supply_rh/100)*(findSatVaporPressure supply_T)

supply_w rh t p = findHumidity (pw rh t) p
-- supply_h supply_w supply_t= findEnthalpy supply_w supply_t

  -- updateFlowConditions
    -- if (section == "right") {
    --   flow_w = recirc_w = supply_w + (room_w - supply_w)*(counter*1.0/STEPS);//supply to room
    --   flow_T = recirc_T = supply_T + (room_T - supply_T)*(counter*1.0/STEPS);//supply to room
    -- } else if (section == "top") {
    --   flow_w = recirc_w = room_w;//stay at room
    --   flow_T = recirc_T = room_T;
    -- } else if (section == "left") {
    --   flow_w = outside_w + (mixed_w - outside_w)*(counter*1.0/STEPS);//outside to mixed
    --   flow_T = outside_T + (mixed_T - outside_T)*(counter*1.0/STEPS);
    --   recirc_w = room_w + (mixed_w - room_w)*(counter*1.0/STEPS);//room to mixed
    --   recirc_T = room_T + (mixed_T - room_T)*(counter*1.0/STEPS);
    -- } else if (section == "bottom") {
    --   flow_w = recirc_w = mixed_w + (supply_w - mixed_w)*Math.pow(counter*1.0/STEPS,2);//mixed to supply quadratically
    --   flow_T = recirc_T = mixed_T + (supply_T - mixed_T)*(counter*1.0/STEPS);
    -- }
toRadix : Int -> Int -> String
toRadix r n =
  let
    getChr c = if c < 10 then toString c else String.fromChar <| Char.fromCode (87+c)

    getStr b = if n < b then getChr n else (toRadix r (n//b)) ++  (getChr (n%b))

  in
    case (r>=2 && r<=16) of
      True -> getStr r
      False -> toString n
