//package com.example.SS2_Backend.model;
//
///* *
//	A simple class to handle extracting input from .xlsx files
// * */
//
//
//public class InputDataDriver {
//	String path;
//	FileInputStream file;
//	XSSFWorkbook workbook;
//	XSSFSheet sheet;
//
//	public InputDataDriver(String path) throws IOException {
//		this.path = path;
//		file = new FileInputStream(path);
//		workbook = new XSSFWorkbook(file);
//		sheet = workbook.getSheetAt(0);
//	}
//
//	public List<Conflict> loadConflictSetFromFile(int row) throws IOException {
//		List<Conflict> conflictSet = new ArrayList<>();
//
//		if (isRowEmpty(row)) {
//			System.out.println("NO CONFLICT SET");
//			return null;
//		}
//		while (!isRowEmpty(row)) {
//			int col = 0;
//			while (isCellExist(row, col)) {
//				String s = getStringOfCoordinate(row, col);
//				if (s != null) {
//
//
//					Conflict conflict = new Conflict(s);
//					conflictSet.add(conflict);
//				}
//				col++;
//			}
//			row++;
//		}
//		return conflictSet;
//	}
//
//	public List<Double> loadNormalPlayerWeights(int startRow) throws IOException {
//		List<Double> weights = new ArrayList<>();
//
//		int numberOfWeights = getValueOfCoordinate(startRow, 1).intValue();
//		for (int i = 0; i < numberOfWeights; i++) {
//			weights.add(getValueOfCoordinate(startRow + 1, i));
//		}
//		return  weights;
//	}
//
//	public List<NormalPlayer> loadNormalPlayersFromFile (int startRow, List<Double> normalPlayerWeights) throws IOException {
//		List<NormalPlayer> normalPlayerList  = new ArrayList<>();
//		int numberOfNormalPlayer = getValueOfCoordinate(startRow, 0).intValue();
//		int numberOfProperties = getValueOfCoordinate(startRow, 1).intValue();
//
//		startRow += 2;
//		for (int i = 0; i < numberOfNormalPlayer; i++) {
//			normalPlayerList.add(setNormalPlayer(startRow, numberOfProperties, normalPlayerWeights));
//			startRow++;
//		}
//		return normalPlayerList;
//	}
//
//	public SpecialPlayer loadSpecialPlayerFromFile(int startRow) throws IOException {
//		SpecialPlayer specialPlayer = new SpecialPlayer();
//		int numberOfProperties = getValueOfCoordinate(startRow, 1).intValue();
//		specialPlayer.setNumberOfProperties(numberOfProperties);
//
//		for (int i = 0; i < specialPlayer.getNumberOfProperties(); i++) {
//			double property = getValueOfCoordinate(startRow + 1, i);
//			double weight = getValueOfCoordinate(startRow + 2, i);
//			specialPlayer.addProperty(property);
//			specialPlayer.addWeight(weight);
//		}
//
//		specialPlayer.setPayoff();
//		return specialPlayer;
//	}
//
//	private boolean isCellExist(int rowIndex, int columnIndex) throws IOException {
//		Row row = sheet.getRow(rowIndex);
//		Cell cell = row.getCell(columnIndex);
//		file.close();
//		return cell != null && getStringOfCoordinate(rowIndex, columnIndex).isBlank();
//	}
//
//	private NormalPlayer setNormalPlayer(int row, int numberOfProperties, List<Double> weights) throws IOException {
//		int strategiesOfPlayer = getValueOfCoordinate(row, 0).intValue();
//		List<Strategy> strategies = new ArrayList<>(strategiesOfPlayer);
//		int column = 1;
//
//		// load the payoff function of this player
//		int columnOfPayoffFunction = strategiesOfPlayer * numberOfProperties + 1;
//		String payoffFunction = getStringOfCoordinate(row, columnOfPayoffFunction);
//
//		// load strategies
//		for (int i = 0; i < strategiesOfPlayer; i++) {
//			Strategy strategy = new Strategy();
//			for (int j = 0; j < numberOfProperties; j++) {
//				// each strategy consists of a list of properties and their weights
//				Double propertyValue = getValueOfCoordinate(row, column);
//				Double propertyWeight = weights.get(j);
//				strategy.addProperty(propertyValue * propertyWeight);
//				column++;
//			}
//			// set payoff for each strategy based on the payoff function
//			strategies.add(strategy);
//		}
//		return new NormalPlayer(strategies, payoffFunction);
//	}
//	//READ VALUE OF A CELL
//	public Double getValueOfCoordinate(int rowIndex, int columnIndex) throws IOException {
//		DataFormatter formatter = new DataFormatter();
//		Row row = sheet.getRow(rowIndex);
//		Cell cell = row.getCell(columnIndex);
//		if (cell == null) {
//			System.err.println("There is an empty cell");
//			return null;
//		}
//
//		file.close();
//		return Double.parseDouble(formatter.formatCellValue(cell));
//	}
//	//READ STRING OF A CELL
//	public String getStringOfCoordinate(int rowIndex, int columnIndex) throws IOException {
//		Row row = sheet.getRow(rowIndex);
//		Cell cell = row.getCell(columnIndex);
//
//		if (cell == null) {
//			System.err.println("There is an empty cell");
//			return null;
//		}
//		file.close();
//		return cell.getStringCellValue();
//	}
//
//	public boolean isRowEmpty(int rowIndex) throws IOException {
//		Row row = sheet.getRow(rowIndex);
//
//		if (row == null) {
//			return true;
//		}
//		if (row.getLastCellNum() <= 0) {
//			return true;
//		}
//		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
//			Cell cell = row.getCell(cellNum);
//			if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
//				return false;
//			}
//		}
//		file.close();
//		return true;
//	}
//}
//