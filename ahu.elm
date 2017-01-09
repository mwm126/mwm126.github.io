-- module Hello exposing (..)

-- import Html exposing (text)

-- main =
--     text "Hello"



-- Read more about this program in the official Elm guide:
-- https://guide.elm-lang.org/architecture/user_input/buttons.html

import Html exposing (Html, div, button, text, label)
import Html.Events exposing (onClick)
import Html.Attributes exposing (style, placeholder)
import Svg exposing (..)
import Svg.Attributes exposing (..)

-- type alias Model = Int

type alias Model = {sa_t : Int,
                        cfm : Int,
                        oa_t : Int,
                        oa_wb : Int,
                        tons : Int,
                        shf : Float,
                        speed : Int
                   }

main =
  Html.beginnerProgram { model = {
                                 sa_t = 62,
                                 cfm = 30000,
                                 oa_t = 90,
                                 oa_wb = 84,
                                 tons = 65,
                                 shf = 0.90,
                                 speed = 5
                             },
                             view = view, update = update }

house =
    let
       rr = 10
       hh = 3*rr
       ww = 8*rr
       xx = 10
       yy = 10
            -- house
       rw = 10
       rh = 10
       xs = [ 0, rw, 2*rw/3, 2*rw/3, -2*rw/3, -2*rw/3, -rw ]
       ys = [ 0, rh, rh,       2*rh,    2*rh, rh,       rh ]
       hp2 = List.map2 (\ x y -> toString (xx+ww+rr+x) ++ "," ++ toString (yy+hh/3+y)) xs ys
       coil_x = xx+ww*0.6
       coil_y = yy+hh
       coil x = line [ x1 (toString (coil_x+x)), y1 (toString (coil_y-rr)), x2 (toString (coil_x+x)), y2 (toString coil_y), stroke "blue" ] []
    in
        [ line [ x1 (toString xx), y1 (toString yy), x2 (toString (xx+ww)), y2 (toString yy), stroke "black" ] []
        , rect [ Svg.Attributes.x (toString xx), Svg.Attributes.y (toString (yy + rr)), width (toString (2*rr)), height (toString (rr)), fill "gray" ] [ ]
        , rect [ Svg.Attributes.x (toString (xx+3*rr)), Svg.Attributes.y (toString (yy + rr)), width (toString (ww-(xx+3*rr))), height (toString (rr)), fill "gray" ] [ ]
        , line [ x1 (toString xx), y1 (toString (yy+hh)), x2 (toString (xx+ww)), y2 (toString (yy+hh)), stroke "black" ] []
        , polygon [ points (String.concat (List.intersperse " " hp2)), stroke "black" ] []
        , coil 10
        , coil 12
        , coil 14
        , coil 16
        , coil 18
        , Svg.text_ [ x (toString coil_x), y (toString coil_y), dx "15", dy "15", fontSize "10", stroke "blue" ] [ Html.text "coil" ]
        ]

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

view : Model -> Html Msg

view model =
  div []
      [ svg [viewBox "0 0 200 200", Svg.Attributes.width "250px" ]
            (List.append house (protractor 150 30 20 30 0.2 60 0.3))
            -- (protractor 150 30 20 30 0.2 60 0.3)
      , div [blueStyle]
            [ div []
                  [ button [ onClick (DecrementSat .sa_t) ] [ Html.text "-" ]
                  , div [] [ Html.text ("SA T " ++ (toString (.sa_t model))) ]
                  , button [ onClick (IncrementSat .sa_t) ] [ Html.text "+" ]
                  ]
            , div []
                [ button [ onClick (DecrementCfm .cfm) ] [ Html.text "-" ]
                , div [] [ Html.text ("CFM " ++ (toString (.cfm model))) ]
                , button [ onClick (IncrementCfm .cfm) ] [ Html.text "+" ]
                ]
            ]
      , Html.text "Adjust weather"
      , div [redStyle]
          [ div []
                [ button [ onClick (DecrementOat .oa_t) ] [ Html.text "-" ]
                , div [] [ Html.text ("OA T " ++ (toString (.oa_t model))) ]
                , button [ onClick (IncrementOat .oa_t) ] [ Html.text "+" ]
                ]
          , div []
              [ button [ onClick (DecrementOawb .oa_wb) ] [ Html.text "-" ]
              , div [] [ Html.text ("OA WB " ++ (toString (.oa_wb model))) ]
              , button [ onClick (IncrementOawb .oa_wb) ] [ Html.text "+" ]
              ]
          ]
      , Html.text "Adjust load"
      , div [grayStyle]
          [ div []
                [ button [ onClick (DecrementTons .tons) ] [ Html.text "-" ]
                , div [] [ Html.text ("Tons " ++ (toString (.tons model))) ]
                , button [ onClick (IncrementTons .tons) ] [ Html.text "+" ]
                ]
          , div []
              [ button [ onClick (DecrementShf .shf) ] [ Html.text "-" ]
              , div [] [ Html.text ("SHF " ++ (toString (.shf model))) ]
              , button [ onClick (IncrementShf .shf) ] [ Html.text "+" ]
              ]
          , div []
              [ button [ onClick (DecrementSpeed .speed) ] [ Html.text "-" ]
              , div [] [ Html.text ("sim speed " ++ (toString (.speed model))) ]
              , button [ onClick (IncrementSpeed .speed) ] [ Html.text "+" ]
              ]
          ]
      ]

blueStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "margin", "10px" )
        -- , ( "padding", "40px" )
        , ( "background-color", "#9999FF" )
        , ( "border", "lightgray solid 1px" )
        ]

redStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "margin", "10px" )
        -- , ( "padding", "40px" )
        , ( "background-color", "#FF9999" )
        , ( "border", "lightgray solid 1px" )
        ]

grayStyle = Html.Attributes.style
        [ ( "font-family", "-apple-system, system, sans-serif" )
        , ( "margin", "10px" )
        -- , ( "padding", "40px" )
        , ( "background-color", "#999999" )
        , ( "border", "lightgray solid 1px" )
        ]

type Msg = IncrementSat (Model->Int)
         | DecrementSat (Model->Int)
         | IncrementCfm (Model->Int)
         | DecrementCfm (Model->Int)
         | IncrementOat (Model->Int)
         | DecrementOat (Model->Int)
         | IncrementOawb (Model->Int)
         | DecrementOawb (Model->Int)
         | IncrementTons (Model->Int)
         | DecrementTons (Model->Int)
         | IncrementShf (Model->Float)
         | DecrementShf (Model->Float)
         | IncrementSpeed (Model->Int)
         | DecrementSpeed (Model->Int)


update : Msg -> Model -> Model

update msg model =
  case msg of
    IncrementSat f -> { model | sa_t = f model + 1}
    DecrementSat f -> { model | sa_t = f model - 1}
    IncrementCfm f -> { model | cfm = f model + 1000}
    DecrementCfm f -> { model | cfm = f model - 1000}
    IncrementOat f -> { model | oa_t = f model + 1}
    DecrementOat f -> { model | oa_t = f model - 1}
    IncrementOawb f -> { model | oa_wb = f model + 3}
    DecrementOawb f -> { model | oa_wb = f model - 3}
    IncrementTons f -> { model | tons = f model + 5}
    DecrementTons f -> { model | tons = f model - 5}
    IncrementShf f -> { model | shf = f model + 0.05}
    DecrementShf f -> { model | shf = f model - 0.05}
    IncrementSpeed f -> { model | speed = f model + 1}
    DecrementSpeed f -> { model | speed = f model - 1}





p = 14.696  -- barometric pressure in psia
cp = 1000      -- specific heat of the room

room_T = 80      -- temperature in Farenheit
supply_T = 56      -- temperature in Farenheit
mixed_T = 83      -- temperature in Farenheit
outside_T = 90      -- temperature in Farenheit

room_rh = 50      -- relative humidity, percentage
supply_rh = 95      -- relative humidity, percentage
mixed_rh = 47      -- relative humidity, percentage
twb = 84      -- outside wet bulb temperature {related to outside relative humidity}

room_w = 0.011      -- pounds water vapor per pound dry air
supply_w = 0.0095      -- pounds water vapor per pound dry air
mixed_w = 0.0152      -- pounds water vapor per pound dry air
outside_w = 0.024      -- pounds water vapor per pound dry air

room_h = 31.2      -- enthalpy in BTUs per pound
supply_h = 23.7      -- enthalpy in BTUs per pound
mixed_h = 36.3      -- enthalpy in BTUs per pound
outside_h = 48.2      -- enthalpy in BTUs per pound

oa_percent = 30      -- percent of outside air

sa_cfm = 30000      -- supply air flow rate

q = 80      -- load in tons
shf = 0.9      -- sensible heat factor [dimensionless]

-- Q_in;
-- SHF_in;

-- flow_w;
-- flow_T;

  -- double recirc_w;
  -- double recirc_T;

  -- String section = "top";
  -- int counter = 0;

  -- AHU ahu;


-- speed = 5;
steps = 20

  -- public void run() {
  --   while(true) {
  --     try {
  --       sleep((int)(1000/SPEED));//sleep for one second
  --     } catch (InterruptedException ie) {}
  --     updateRoomConditions();//update stuff, should be "synchronized"
  --     updateFlowConditions();
  --     counter++;
  --     if (counter==STEPS) {
  --       counter=0;
  --       if (section == "right") {
  --         section = "top";
  --       } else if (section == "top") {
  --         section = "left";
  --       } else if (section == "left") {
  --         section = "bottom";
  --       } else if (section == "bottom") {
  --         section = "right";
  --       }
  --     }
  --     ahu.repaint();
  --   }//end infinite while loop
  -- }//end run()

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

{--
   void updateRoomConditions() {
    double pw = (room_rh/100)*findSatVaporPressure(room_T);

    double cool_supply = sa_cfm*60*0.241*(room_T - supply_T)/(13.2*12000);

    //double w_in = 42;//meaning of life, the universe, everything...

    Q_in = sa_cfm*60*(room_h - supply_h)/(13.2*12000);
    SHF_in = cool_supply/Q_in;

    room_T = room_T + (Q*SHF - cool_supply)/cp;

    long now = System.currentTimeMillis()/1000;
    //System.out.println("now = "+now);

    if (now%6==3) {
      //System.out.println("temp time");
      if (room_T>80)
        AHU.print("Whew!  It's too hot in here!");
      else if (room_T<70)
        AHU.print("Brrr!  It's too cold in here!");
      else
        AHU.print("");
    }


    room_w = findHumidity(pw, p);
    room_h = findEnthalpy(room_w, room_T);


    room_rh = room_rh +
    (Q*(1-SHF) - (room_w - supply_w)*sa_cfm*60*(1093+.444*supply_T)/(13.2*12000))/100;

    if (now%6==0) {
      if (room_rh>60)
        AHU.print("Ugh!  It's too humid.");
      else if (room_rh<30)
        AHU.print("It's too dry.");
      else
        AHU.print("");
    }


    updateMixedConditions();
  }

  void updateOutsideConditions() {
    //double pws = findSatVaporPressure(outside_T);
    double pw_star = findSatVaporPressure(Twb);
    double w_star = findHumidity(pw_star, p);
    outside_w = ((1093 - 0.556*Twb)*w_star - 0.24*(outside_T - Twb))/(1093 + 0.444*outside_T - Twb);
    outside_h = findEnthalpy(outside_w, outside_T);
  }

  void updateMixedConditions() {
    mixed_T = (OA_percent/100)*outside_T + (1-OA_percent/100)*room_T;
    mixed_w = (OA_percent/100)*outside_w + (1-OA_percent/100)*room_w;
    //double pws = findSatVaporPressure(mixed_T);
    mixed_h = findEnthalpy(mixed_w, mixed_T);
  }

  void updateSupplyConditions() {
    if (supply_T<60) {
      supply_rh = 95;
    } else {
      supply_rh = 95 - (supply_T - 60)*3;
    }
    double pw = (supply_rh/100)*findSatVaporPressure(supply_T);
    supply_w = findHumidity(pw, p);
    supply_h = findEnthalpy(supply_w, supply_T);
  }

  void updateFlowConditions() {
    if (section == "right") {
      flow_w = recirc_w = supply_w + (room_w - supply_w)*(counter*1.0/STEPS);//supply to room
      flow_T = recirc_T = supply_T + (room_T - supply_T)*(counter*1.0/STEPS);//supply to room
    } else if (section == "top") {
      flow_w = recirc_w = room_w;//stay at room
      flow_T = recirc_T = room_T;
    } else if (section == "left") {
      flow_w = outside_w + (mixed_w - outside_w)*(counter*1.0/STEPS);//outside to mixed
      flow_T = outside_T + (mixed_T - outside_T)*(counter*1.0/STEPS);
      recirc_w = room_w + (mixed_w - room_w)*(counter*1.0/STEPS);//room to mixed
      recirc_T = room_T + (mixed_T - room_T)*(counter*1.0/STEPS);
    } else if (section == "bottom") {
      flow_w = recirc_w = mixed_w + (supply_w - mixed_w)*Math.pow(counter*1.0/STEPS,2);//mixed to supply quadratically
      flow_T = recirc_T = mixed_T + (supply_T - mixed_T)*(counter*1.0/STEPS);
    }
  }

  public void update_everything() {
    updateOutsideConditions();
    updateMixedConditions();
    updateSupplyConditions();
    updateFlowConditions();
    //print_controls();
    //print_everything();
    ahu.update_everything();
  }
}
--}
