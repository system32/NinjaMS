/* Author: Xterminator
	NPC Name: 		Sera
	Map(s): 		Maple Road : Entrance - Mushroom Town Training Camp (0), Maple Road: Upper level of the Training Camp (1)
	Description: 		First NPC
*/

function start() {
	cm.sendOk("Hey there. If you have 100 slime bubble and 100 snail shells 100 teddy bear cotton I can give you a free pet with meso magnet and item pouch")
}
function action() {
	cm.sendOk("Bye now. Take Care");
	cm.gainItem(5000000);
	cm.dispose();
}
