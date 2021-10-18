import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class LiquidEng {
    public static void main(String[] args){
        String line = "";
        String splitBy = ",";
        String LiquidType = "";
        String ticker = "";
        ArrayList<String>[] dummy = new ArrayList[10];
        int length = 0;

        try{
            BufferedReader br = new BufferedReader(new FileReader("LIFO-Test-Input.csv")); //read the file
            int i = 0;
            while ((line = br.readLine())!= null){
                String[] transaction = line.split(splitBy);
                //System.out.println("Transaction " +  transaction[0] + " -> Date : " + transaction[1] + " -> Trade Price: " + transaction[2] + " -> Trade Quantity: " + transaction[3] + " -> Transaction Type: " + transaction[4] + " -> EOD Price: " + transaction[5] + " -> Position Quantity" + transaction[6]);
                if(i == 1){
                    LiquidType = transaction[12]; 
                    ticker = transaction[13];
                }
                dummy[i] = new ArrayList<String>();
                for(int j = 0; j< 6; j++ ){
                    dummy[i].add(transaction[j]);
                }
                i = i + 1;
                length = i;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        /*
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < dummy[i].size(); j++) {
                System.out.print(dummy[i].get(j) + " ");
            }
            System.out.println();
        } */

        System.out.println("Liquidation Type is " + LiquidType + " & ticker is " + ticker);
        int PosQuant = 0;
        float TotalCost = 0;
        int TradePrice = 0;
        int TradeQuant = 0;
        float EOD = 0;
        String TransType = "";
        float AvgCost = 0;
        int TotalRealized = 0;
        float MarketVal = 0;
        float TotalUnrealized = 0;
        int PositionLeft = 0;
        dummy[0].add(6, "Position Quantity");
        dummy[0].add(7, "Total Cost");
        dummy[0].add(8, "Average Cost");
        dummy[0].add(9, "Total Realized");
        dummy[0].add(10, "Market Value");
        dummy[0].add(11, "Total Unrealized");
        dummy[0].add(12, "Liquidation Method");
        dummy[0].add(13, "Ticker");
        dummy[0].add(14, "Position Left");

        for(int i = 1; i< length; i++){
            for (int p = 6; p<16; p++){
                dummy[i].add(p, String.valueOf(0));
            }
        }

        

        TradePrice = Integer.parseInt(dummy[1].get(2)); //given
        TradeQuant = Integer.parseInt(dummy[1].get(3)); //given
        TransType = dummy[1].get(4); //given
        EOD = Float.parseFloat(dummy[1].get(5)); //given

        PosQuant = TradeQuant; 
        TotalCost = TradeQuant*TradePrice;
        AvgCost = TradePrice;
        TotalRealized = 0;
        MarketVal = EOD*PosQuant;
        TotalUnrealized = MarketVal - TotalCost;
        PositionLeft = PosQuant;
        
        
        dummy[1].set(6, String.valueOf(PosQuant));
        dummy[1].set(7, String.valueOf(TotalCost));
        dummy[1].set(8, String.valueOf(AvgCost));
        dummy[1].set(9, String.valueOf(TotalRealized));
        dummy[1].set(10, String.valueOf(MarketVal));
        dummy[1].set(11, String.valueOf(TotalUnrealized)); 
        dummy[1].set(12, LiquidType);
        dummy[1].set(13, ticker);
        dummy[1].set(14, String.valueOf(PositionLeft)); 

        

        

    
        if(LiquidType.equals("FIFO")){

            for(int i = 2; i<length; i++){ //start from first transaction
                TradePrice = Integer.parseInt(dummy[i].get(2)); //given
                TradeQuant = Integer.parseInt(dummy[i].get(3)); //given
                TransType = dummy[i].get(4); //given
                EOD = Float.parseFloat(dummy[i].get(5)); //given
    

                
                if(dummy[i].get(4).equals("Buy")){
                    PosQuant = Integer.parseInt(dummy[i-1].get(6)) + TradeQuant;
                    TotalCost = Float.parseFloat(dummy[i-1].get(7)) + TradeQuant*TradePrice;
                    PositionLeft = TradeQuant;
                    AvgCost = TotalCost/PosQuant;
                    TotalRealized = Integer.parseInt(dummy[i-1].get(9));
                    MarketVal = EOD*PosQuant;
                    TotalUnrealized = MarketVal - TotalCost;
                }
                
                
                if(dummy[i].get(4).equals("Sell")){
                    PosQuant = Integer.parseInt(dummy[i-1].get(6)) - TradeQuant;
                    PositionLeft = 0;
                    MarketVal = EOD*PosQuant;


                    //temp variables for FIFO logic
                    float tempTotalCost = Float.parseFloat(dummy[i-1].get(7));
                    int tempTradeQuant = TradeQuant;
                    int tempRealized = Integer.parseInt(dummy[i-1].get(9));;
                    for(int counter = 1; counter <= i; counter++){ //iterates from first transacation date till row of selling
                        
                        if(tempTradeQuant<=Integer.parseInt(dummy[counter].get(14))){ //compares position left from transaction date and quantity being traded today
                            TotalCost = tempTotalCost -  (tempTradeQuant*Integer.parseInt(dummy[counter].get(2))); //if current trade quant is less, then the total cost is calculated
                            dummy[counter].set(14, String.valueOf(Integer.parseInt(dummy[counter].get(14)) - tempTradeQuant));
                            TotalRealized = tempTradeQuant*(Integer.parseInt(dummy[i].get(2)) - Integer.parseInt(dummy[counter].get(2)))+ tempRealized;
                            break;
                        }

                        else{
                            tempTotalCost = tempTotalCost - (Integer.parseInt(dummy[counter].get(14))*Integer.parseInt(dummy[counter].get(2)));
                            tempTradeQuant = tempTradeQuant - Integer.parseInt(dummy[counter].get(14));
                            tempRealized = tempRealized + Integer.parseInt(dummy[counter].get(14))*(Integer.parseInt(dummy[i].get(2)) - Integer.parseInt(dummy[counter].get(2)));
                            dummy[counter].set(14, String.valueOf(0));
                        } 
                    }
                    if(PosQuant == 0){AvgCost = 0;}
                    else{AvgCost = TotalCost/PosQuant;}

                    TotalUnrealized = MarketVal - TotalCost;                    
                } 

                dummy[i].set(6, String.valueOf(PosQuant));
                dummy[i].set(7, String.valueOf(TotalCost));
                dummy[i].set(8, String.valueOf(AvgCost));
                dummy[i].set(9, String.valueOf(TotalRealized));
                dummy[i].set(10, String.valueOf(MarketVal));
                dummy[i].set(11, String.valueOf(TotalUnrealized));             
                dummy[i].set(14, String.valueOf(PositionLeft)); 
                
            }

        } 

        if(LiquidType.equals("LIFO")){

            for(int i = 2; i<length; i++){ //start from first transaction
                TradePrice = Integer.parseInt(dummy[i].get(2)); //given
                TradeQuant = Integer.parseInt(dummy[i].get(3)); //given
                TransType = dummy[i].get(4); //given
                EOD = Float.parseFloat(dummy[i].get(5)); //given
    

                
                if(dummy[i].get(4).equals("Buy")){
                    PosQuant = Integer.parseInt(dummy[i-1].get(6)) + TradeQuant;
                    TotalCost = Float.parseFloat(dummy[i-1].get(7)) + TradeQuant*TradePrice;
                    PositionLeft = TradeQuant;
                    AvgCost = TotalCost/PosQuant;
                    TotalRealized = Integer.parseInt(dummy[i-1].get(9));
                    MarketVal = EOD*PosQuant;
                    TotalUnrealized = MarketVal - TotalCost;
                }
                
                
                if(dummy[i].get(4).equals("Sell")){
                    PosQuant = Integer.parseInt(dummy[i-1].get(6)) - TradeQuant;
                    PositionLeft = 0;
                    MarketVal = EOD*PosQuant;


                    //temp variables for FIFO logic
                    float tempTotalCost = Float.parseFloat(dummy[i-1].get(7));
                    int tempTradeQuant = TradeQuant;
                    int tempRealized = Integer.parseInt(dummy[i-1].get(9));;
                    for(int counter = 1; counter <= i; counter++){ //iterates from first transacation date till row of selling
                        
                        if(tempTradeQuant<=Integer.parseInt(dummy[i -counter].get(14))){ //compares position left from transaction date and quantity being traded today
                            TotalCost = tempTotalCost -  (tempTradeQuant*Integer.parseInt(dummy[i - counter].get(2))); //if current trade quant is less, then the total cost is calculated
                            dummy[i - counter].set(14, String.valueOf(Integer.parseInt(dummy[i - counter].get(14)) - tempTradeQuant));
                            TotalRealized = tempTradeQuant*(Integer.parseInt(dummy[i].get(2)) - Integer.parseInt(dummy[i-counter].get(2)))+ tempRealized;
                            break;
                        }

                        else{
                            tempTotalCost = tempTotalCost - (Integer.parseInt(dummy[i-counter].get(14))*Integer.parseInt(dummy[i-counter].get(2)));
                            tempTradeQuant = tempTradeQuant - Integer.parseInt(dummy[i-counter].get(14));
                            tempRealized = tempRealized + Integer.parseInt(dummy[i-counter].get(14))*(Integer.parseInt(dummy[i].get(2)) - Integer.parseInt(dummy[i-counter].get(2)));
                            dummy[i-counter].set(14, String.valueOf(0));
                        } 
                    }
                    if(PosQuant == 0){AvgCost = 0;}
                    else{AvgCost = TotalCost/PosQuant;}

                    TotalUnrealized = MarketVal - TotalCost;                    
                } 

                dummy[i].set(6, String.valueOf(PosQuant));
                dummy[i].set(7, String.valueOf(TotalCost));
                dummy[i].set(8, String.valueOf(AvgCost));
                dummy[i].set(9, String.valueOf(TotalRealized));
                dummy[i].set(10, String.valueOf(MarketVal));
                dummy[i].set(11, String.valueOf(TotalUnrealized));             
                dummy[i].set(14, String.valueOf(PositionLeft)); 
                
            }
    }


        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 15; j++) {
                System.out.print(dummy[i].get(j) + " ");
            }
            System.out.println();
        }
        //setting variables for calculations
        

        
    }
}


