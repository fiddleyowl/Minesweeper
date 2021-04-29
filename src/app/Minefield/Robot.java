package app.Minefield;

import java.util.Random;

import static app.PublicDefinitions.*;

public class Robot {

    //region Normal Robot

    Random random = new Random();

    public void clickRandomly(AgainstAIController againstAIController) {
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickRandomly(againstAIController);
        } else {
            int temp = random.nextInt(4);
            //Flag the spot with a percentage of 25%.
            if (temp == 0) {
                againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    public void clickLikeAProfessor(AgainstAIController againstAIController) {
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickLikeAProfessor(againstAIController);
        } else {
            if (againstAIController.minefield[i][j] == MinefieldType.MINE) {
                againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    public void clickLikeAAmateur(AgainstAIController againstAIController) {
        boolean probability = random.nextInt(3) == 0;
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickLikeAAmateur(againstAIController);
        } else {
            if (againstAIController.minefield[i][j] == MinefieldType.MINE) {
                if (probability) {
                    againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
                } else {
                    againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
                }
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    //endregion

    //region MSolver

    class MSolver{

        /**
         *
         * @param manipulatedMinefield
         * @param i for row
         * @param j for column
         * @return
         */
        static int countUnopenedSquaresAround(LabelType[][] manipulatedMinefield,int i,int j){
            int unopenedSquares = 0;

            try { if (manipulatedMinefield[i-1][j-1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[i-1][ j ] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[i-1][j+1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[ i ][j+1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[i+1][j+1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[i+1][ j ] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[i+1][j-1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}
            try { if (manipulatedMinefield[ i ][j-1] != LabelType.BOMBED && manipulatedMinefield[i-1][j-1] != LabelType.CLICKED){ unopenedSquares++; } }catch (Exception ignored){}

            return unopenedSquares;
        }

    }

    //endregion
}
