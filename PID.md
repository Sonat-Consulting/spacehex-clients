PID på 5 minutter 
----------------------
PID-kontrollere ([PID-regulator](https://snl.no/PID-regulator) på norsk) er arbeidshesten når det gjelder å gå fra 1 tilstand til en annen (feks fra en posisjon til en annen).
PID står for Proportional (P), Integral (I) og Derivative (D). Disse er input til kontroller.

### PID for oppgaven
For oppgaven vår så kan vi ignorerere I (Integral) og start med en PD kontroller.

* Proportional: Mål på hvor langt er vi fra målet.
* Derivative: Den deriverte, som her vil være et mål på endring i hvor langt vi er fra målet over tid. (Flere typer målinger av endring kan brukes, siste endring i proportional er en mulighet)

Hver av disse vil vektes, og vekting avgjør hvordan kontroller oppfører seg.

* proportionalVekt: Jo høyere, ho mer prioriteres feilen akkuratt nå. Overskyting kan skje.
* derivativeVekt: Jo høyere jo mer tar algoritme hensyn til hvordan feilraten vil være i fremtiden. Dette demper svingninger.

### Kalkulering av proprotional (P)
Proportional måler hvor langt vi er fra målet, et mål i x aksen vil da være.
```proportional = goal.x - posisiton.x```

### Kalkulering av derivative (D)
Et mulig mål er endring i proportional fra forrige tick.
```derivative = (proportional - previousProportional)/timeDelta```

### Oversettelse fra kontroller output til aksjon
Kontroller vil gi ut en verdi. Vektene her er konstanter som tilpasses problemet.
``` 
proportionalVekt = 1.0
derivativeVekt = 3.0
output = proportional*proportionalVekt + derivative*derivativeVekt 
```
I vårt tilfelle så må vi da lage en akselrasjons aksjon som prøver oppnå dette målet basert på output.

#### Bestemme vekter:
Gjør det eksperimentelt. Hva som er bra er avhengig av andre valg. Som hvor nær lanskapet stien som er valgt er, og hvor høy maksimal hastighet som er akseptabel er o.s.v

#### Hva PID kontroller ikke hjelper oss med
PID kontroller tar fartøy fra en posisjon til en annen posisjon. Den sier ikke noe om når den har oppnådd dette. Den tar ikke hensyn til om den har kollidert med noe på veien. Å gjøre ting uten overskyting kan gå sakte.

[PID without a PHD](https://web.archive.org/web/20250226141816/www.wescottdesign.com/articles/pid/pidWithoutAPhd.pdf)




