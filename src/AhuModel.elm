module AhuModel exposing (..)
import Time exposing (Time, second)

type alias Model = { sa_t : Float -- supply air temperature
                   , oa_p : Float --outside air percentage, from 0.0 to 100.0
                   , cfm : Float -- supply air flow rate, cubic feet per minute
                   , oa_t : Float -- outside air temperature in Fahrenheit
                   , oa_wb : Float -- outside air web bulb, function of outside humidity
                   , tons : Float -- building cooling load, tons of ice melting per day
                   , shf : Float -- sensible heat factor qsense/qtotal, dimensionless from 0.0 to 1.0
                   , cycle : Int
                   , time : Float -- value between 0.0 and 1.0
                   , room_rh : Float -- room relative humidity percentage, from 0.0 to 100.0
                   , room_t : Float -- room temperature in Fahrenheit
                   , room_h : Float -- room enthalpy in BTUs per pound
                   }

init : (Model, Cmd Msg)
init = (
        { sa_t = 62
        , oa_p = 30
        , cfm = 30000
        , oa_t = 90
        , oa_wb = 84
        , tons = 65
        , shf = 0.90
        , cycle = 10
        , time = 0
        , room_rh = 50
        , room_t = 80
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

pressure = 14.696 -- barometric pressure in psia

h2o_saturation_vapor_pressure farenheit_temperature =
-- convert to Rankine
    let t = farenheit_temperature + 459.67
    in
        -- sat vapor pressure
        e^(-10440.4/t - 11.29465
          - 0.027022355*t
              + 1.289036e-5*t*t
                  - 2.4780681e-9*t*t*t+6.5459673*logBase e t)

-- w is absolute humidity
-- t is temperature
enthalpy w t = ((0.24+0.444*w)*t + 1061*w)

-- mass of water vapor per mass of air; dimensionless
abs_humidity rel_humidity temperature air_partial_pressure =
    let
-- partial pressure of water
        h2o_partial_pressure = (rel_humidity/100)*(h2o_saturation_vapor_pressure temperature)
    in
        0.62198*(h2o_partial_pressure/(air_partial_pressure - h2o_partial_pressure))


cfpp = 13.2 -- cubic_ft_per_pound
mph = 60 -- 60 minutes per hour

-- total heat flow in
total_heat_flow sa_cfm room_w room_t supply_w supply_t=
  let
    room_h = enthalpy room_w room_t -- btu per pound
    supply_h = enthalpy supply_w supply_t
  in
    sa_cfm*mph*(room_h - supply_h)/(cfpp*btu_per_ton)

-- btu per (lb.deg F)
air_specific_heat = 0.241


btu_per_ton = 12000

-- sensible cool supply in tons
cool_supply model =
    let
        cp = air_specific_heat
    in
        model.cfm*60*cp*(model.room_t - model.sa_t)/(cfpp*btu_per_ton)


new_room_t model =
    let
        cp = air_specific_heat
    in
        model.room_t + (((model.tons*model.shf - cool_supply model)*btu_per_ton/cp)/1000000.0)


room_t old_room_t q shf cool_supply cp = old_room_t + (q*shf - cool_supply)/cp


room_h room_w room_t = enthalpy room_w room_t

new_room_rel_humidity model =
    let
        q = model.tons
        shf = model.shf
        sa_cfm = model.cfm
        room_w = abs_humidity model.room_rh model.room_t pressure
        supply_t = model.sa_t
        supply_w = abs_humidity (supply_rel_humidity model.sa_t) model.sa_t pressure
        something = (1093 - 0.444*supply_t)/(13.2*12000)
    in
        model.room_rh +
            (q*(1-shf) - (room_w - supply_w)*sa_cfm*60*something)/100


room_comment model =
    let
        room_rh = model.room_rh
        room_t = model.room_t
    in
      if room_rh>60 then
          "Ugh!  It's too humid."++(toString room_rh )
      else if room_rh<30 then
              "It's too dry."++(toString room_rh )
          else if room_t>80 then
                    "Whew!  It's too hot in here!"++(toString room_t )
                else if room_t<70 then
                        "Brrr!  It's too cold in here!"++(toString room_t )
                    else
                        ""

outside_w model =
    let
        pw_star = h2o_saturation_vapor_pressure model.oa_wb
        w_star model = abs_humidity pw_star model.oa_t pressure
    in
        ((1093 - 0.556*model.oa_wb)*(w_star model) - 0.24*(model.oa_t - model.oa_wb))/(1093 + 0.444*model.oa_t - model.oa_wb)


supply_rel_humidity supply_t = if supply_t<60 then
                          95
                      else
                          95 - (supply_t - 60)*3


supply_w rh t p = abs_humidity  rh t p
