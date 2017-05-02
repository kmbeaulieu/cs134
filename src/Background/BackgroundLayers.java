package Background;

public class BackgroundLayers {
	//128x15 for the bgtree
	public static final int BGTREELENGTH = 128;
	public static final int BGTREEHEIGHT= 15;
	public static int[][] backgroundTrees = new int[][]{
		{69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69},
		{69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69},
		{69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69},
		{69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69, 1, 2, 3,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69},
		{69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69,10,11,12,69,69,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69,10,11,12,69,69,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69,10,11,12,69,69,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69, 4, 5, 6, 7, 8, 9,69,69,69,69,69,10,11,12,69,69,69,69,69},
		{69,69,13,14,15,16,17,18,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,19,20,21,22,69,69,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,19,20,21,22,69,69,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,19,20,21,22,69,69,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,69,13,14,15,16,17,18,69,69,69,69,19,20,21,22,69,69,69,69,69},
		{69,23,24,25,26,27,28,29,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,30,31,32,33,69,69,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,30,31,32,33,69,69,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,30,31,32,33,69,69,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,23,24,25,26,27,28,29,69,69,69,69,30,31,32,33,69,69,69,69,69},
		{69,34,35,36,37,38,39,40,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,41,42,43,44,69,69,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,41,42,43,44,69,69,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,41,42,43,44,69,69,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,34,35,36,37,38,39,40,69,69,69,69,41,42,43,44,69,69,69,69,69},
		{45,46,47,48,49,50,51,52,53,54,55,56,46,47,48,49,50,51,52,55,56,53,54,46,47,48,57,55,56,53,54,55,45,46,47,48,49,50,51,52,53,54,55,56,46,47,48,49,50,51,52,55,56,53,54,46,47,48,57,55,56,53,54,55,45,46,47,48,49,50,51,52,53,54,55,56,46,47,48,49,50,51,52,55,56,53,54,46,47,48,57,55,56,53,54,55,45,46,47,48,49,50,51,52,53,54,55,56,46,47,48,49,50,51,52,55,56,53,54,46,47,48,57,55,56,53,54,55},
		{58,59,60,60,60,60,60,61,62,63,64,62,59,60,60,60,60,60,61,64,65,62,63,59,60,60,61,64,65,62,63,64,58,59,60,60,60,60,60,61,62,63,64,62,59,60,60,60,60,60,61,64,65,62,63,59,60,60,61,64,65,62,63,64,58,59,60,60,60,60,60,61,62,63,64,62,59,60,60,60,60,60,61,64,65,62,63,59,60,60,61,64,65,62,63,64,58,59,60,60,60,60,60,61,62,63,64,62,59,60,60,60,60,60,61,64,65,62,63,59,60,60,61,64,65,62,63,64},
		{66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,66,66,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,66,66,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,66,66,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,67,67,67,66,66,66,66,66,66,67,67,66,66,66,66,66,66},
		{68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68},
		{68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68},
		{68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68},
		{68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68},
	};
	//128x3 for the bgbush
	public static final int BGBUSHLENGTH = 128;
	public static final int BGBUSHHEIGHT= 3;
//	Tile[][] backgroundBushes = new Tile[][]{
//		{69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78,69,69,70,71,72,69,73,74,75,69,76,77,78},
//		{79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90,91,79,80,81,82,83,84,85,86,87,88,89,90},
//		{92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103,104,92,93,94,95,96,97,94,99,100,101,102,103}
//	};

	
	public static final String FTL = "res\\backgrounds\\bgTrees\\"; //file tree location
        public static final String FLL = "res\\backgrounds\\bgLevel\\"; //file level location
	public static final String FBL = "res\\backgrounds\\bgBushes\\"; //file bushes location
	public static final String FNE = ".tga"; //file name ending
}
