import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VirusInformation {
    public boolean isAutoPickupEnabled = false;
    public int Delay = 300;
    public int lifeTime = 0;
    public double innerInfectabilityRate = 0.007;
    public double medicineEffect = 0;
    public double mortality = 0.001;
    public double popupChance = 0.003;

    public double[] transportWayToInfection; //LAND,AIR,SEA

    public double[] climateInfectability; //COLD,MEDIOCRE,HOT
    public double [] transportationChances;

    public HashMap<Integer, List<Mutation>> mutations;

    public VirusInformation(){
         mutations = new HashMap<>();
         initMutations();

         transportWayToInfection = new double[]{1,1,1};
         climateInfectability = new double[]{1,1,1};
         transportationChances = new double[]{0.5,0.5,0.5};

        //initial infection
         Main.countries.get((int) (Math.random()*Main.countries.size())).infected = 50;


    }
    public void initMutations(){
        Mutation coldClimateInfectabilityIncreased = new Mutation("Increase virus spread in cold climate, ", 0){
            @Override
            public void apply(){
                super.apply();
                climateInfectability[0] += 0.12;
            }
        };
        Mutation mediocreClimateInfectabilityIncreased = new Mutation("Increase virus spread in mediocre climate", 0){
            @Override
            public void apply(){
                super.apply();
                climateInfectability[1] += 0.15;
            }
        };
        Mutation hotClimateInfectabilityIncreased = new Mutation("Increase virus spread in hot climate", 0){
            @Override
            public void apply(){
                super.apply();
                climateInfectability[2] += 0.1;
            }
        };

        Mutation landInfectabilityIncreased = new Mutation("Virus spread via land.", 1,
                hotClimateInfectabilityIncreased,
                mediocreClimateInfectabilityIncreased,
                coldClimateInfectabilityIncreased){
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[0] += 0.2;
            }
        };;
        Mutation landInfectabilityIncreased2 = new Mutation("Virus spread via land travel II", 2, landInfectabilityIncreased){
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[0] += 0.3;
            }
        };
        Mutation landInfectabilityIncreased3 = new Mutation("Virus spread via land travel III", 3, landInfectabilityIncreased2)
        {
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[0] += 0.5;
            }
        };;;
        Mutation airInfectabilityIncreased = new Mutation("Virus spread via air travel", 1,
                hotClimateInfectabilityIncreased,
                mediocreClimateInfectabilityIncreased,
                coldClimateInfectabilityIncreased){
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[1] += 0.2;
            }
        };
        Mutation airInfectabilityIncreased2 = new Mutation("Increased virus spread via air travel II", 2, airInfectabilityIncreased){
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[1] += 0.3;
            }
        };
        Mutation airInfectabilityIncreased3 = new Mutation("Increased virus spread via air travel III", 3, airInfectabilityIncreased2){
            @Override
            public void apply(){
                super.apply();
                transportWayToInfection[1] += 0.5;
            }
        };
        Mutation innerInfectabilityRateIncrease = new Mutation("Increased virus spread via human interaction", 3, airInfectabilityIncreased2, landInfectabilityIncreased2){
            @Override
            public void apply(){
                super.apply();
                innerInfectabilityRate+=0.003;
            }
        };
        Mutation mutationSpeedIncrease = new Mutation("Increased rate of mutations by "+(int)((Delay /200-1)*100)+"%", 4, airInfectabilityIncreased3, landInfectabilityIncreased3){
            @Override
            public void apply(){
                super.apply();
                Main.v.Delay = 200;
            }
        };
        Mutation mortalityIncrease = new Mutation("Increased mortality", 4, airInfectabilityIncreased3, landInfectabilityIncreased3){
            @Override
            public void apply(){
                super.apply();
                Main.v.mortality *=2;
            }
        };

        //END OF  VIRUS  MUTATIONS//

       String text=" Buy more medical equipment";
        Mutation medicineImprovement = new Mutation("Buy more medical equipment", 0, 16,true){
            @Override
            public void apply(){

                if (isActive&&!applied) {
                    Main.v.medicineEffect += 0.001;
                }
                super.apply();
            }
        };

        Mutation medicineImprovement2 = new Mutation("Provide masks", 1, 36,true, medicineImprovement){
            @Override
            public void apply(){

                if (isActive&&!applied) {
                    Main.v.medicineEffect += 0.001;
                }
                super.apply();
            }
        };

        Mutation bonusPopupFreqIncrease = new Mutation("Increases frequency of popup bonuses",0,17,true) {
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.popupChance += 0.001;
                }
                super.apply();

            }
        };
        Mutation investigationBegin = new Mutation("Start investigating the virus in labs", 2,21, true,medicineImprovement2);

        Mutation increaseMaxPopupValue = new Mutation("Increase amount of OmegaCoins in bonuses", 2, 25, true, bonusPopupFreqIncrease){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Country.BonusPopup.maxValue += 1;
                }
                super.apply();

            }
        };
        Mutation reduceTransportRate = new Mutation("Discourage travelling",3,29, true, investigationBegin,increaseMaxPopupValue){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.transportationChances[0] -= 0.03;
                    Main.v.transportationChances[1] -= 0.03;
                    Main.v.transportationChances[2] -= 0.02;
                }
                super.apply();

            }
        };
        Mutation increaseMaxPopupValue2 = new Mutation("Increase amount of OmegaCoins in bonuses", 3, 31, true, investigationBegin,increaseMaxPopupValue){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Country.BonusPopup.maxValue += 2;
                }
                super.apply();

            }
        };
        Mutation reduceAirTransport = new Mutation("Reduces plane traffic", 4, 35, true, reduceTransportRate){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.transportationChances[1] -= 0.05;
                }
                super.apply();

            }
        };
        Mutation reduceWaterTransport = new Mutation("Reduces water traffic", 4, 40, true, reduceTransportRate){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.transportationChances[2] -= 0.06;
                }
                super.apply();

            }
        };
        Mutation encourageInnerQuarantine = new Mutation("Encourage self-quarantine", 4, 29, true,reduceTransportRate){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.innerInfectabilityRate -= 0.001;
                }
                super.apply();

            }
        };

        Mutation enableCuring = new Mutation("Start curing the infected", 5, 190, true, encourageInnerQuarantine){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.medicineEffect += 0.04;
                    Main.v.mortality/=1.5;
                }
                super.apply();

            }
        };
        Mutation totalQuarantine = new Mutation("Establish quarantine", 5, 175, true,reduceAirTransport, encourageInnerQuarantine){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.innerInfectabilityRate = 0.001;
                    Main.v.transportationChances = new double[]{0.04, 0.04, 0.04};
                }
                super.apply();

            }
        };
        Mutation enableAutoPickup = new Mutation("Automatically picks up Bonuses", 5, 130, true, reduceWaterTransport, encourageInnerQuarantine){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.isAutoPickupEnabled = true;
                }
                super.apply();

            }
        };
        Mutation enableCuring2 = new Mutation("Advanced curing the infected", 6, 230, true, enableCuring){
            @Override
            public void apply() {
                if (isActive&&!applied) {
                    Main.v.medicineEffect += 0.0375;
                    Main.v.mortality/=1.5;
                }
                super.apply();

            }
        };
        mutations = (HashMap<Integer, List<Mutation>>) Stream.of(
                hotClimateInfectabilityIncreased,
                mediocreClimateInfectabilityIncreased,
                coldClimateInfectabilityIncreased,
                landInfectabilityIncreased,
                landInfectabilityIncreased2,
                landInfectabilityIncreased3,
                airInfectabilityIncreased,
                airInfectabilityIncreased2,
                airInfectabilityIncreased3,
                innerInfectabilityRateIncrease,
                mortalityIncrease,
                mutationSpeedIncrease).
                collect(Collectors.groupingBy((Mutation m)->m.level));

        Main.upgrades = (HashMap<Integer, List<Mutation>>) Stream.of(
                medicineImprovement,
                medicineImprovement2,
                reduceAirTransport,
                reduceWaterTransport,
                bonusPopupFreqIncrease,
                investigationBegin,
                increaseMaxPopupValue,
                reduceTransportRate,
                encourageInnerQuarantine,
                increaseMaxPopupValue2,
                totalQuarantine,
                enableCuring,
                enableAutoPickup,
                enableCuring2)
                .collect(Collectors.groupingBy((Mutation m)->m.level));
    }

}