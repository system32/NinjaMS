/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.server.constants;

/**
 *
 * @author Danny
 */
public class Skills {

    public final static int[] BeginnerSkills = {Beginner.FollowTheLead, Beginner.BlessingOfTheFairy, Beginner.ThreeSnails,
        Beginner.Recovery, Beginner.NimbleFeet, Beginner.LegendarySpirit, Beginner.MonsterRider, Beginner.EchoOfHero,
        Beginner.JumpDown, Beginner.Maker, /*Beginner.BambooRain, */Beginner.Invincibility, Beginner.PowerExplosion};

    public class Beginner {

        public final static int FollowTheLead = 8;
        public final static int BlessingOfTheFairy = 12;
        public final static int ThreeSnails = 1000;
        public final static int Recovery = 1001;
        public final static int NimbleFeet = 1002;
        public final static int LegendarySpirit = 1003;
        public final static int MonsterRider = 1004;
        public final static int EchoOfHero = 1005;
        public final static int JumpDown = 1006;
        public final static int Maker = 1007;
        public final static int BambooRain = 1009;
        public final static int Invincibility = 1010;
        public final static int PowerExplosion = 1011;
    }
    public final static int[] SwordsmanSkills = {Swordsman.ImprovedHPRecovery, Swordsman.ImprovedMaxHPIncrease, Swordsman.Endure,
        Swordsman.IronBody, Swordsman.PowerStrike, Swordsman.SlashBlast};

    public class Swordsman {

        public final static int ImprovedHPRecovery = 1000000;
        public final static int ImprovedMaxHPIncrease = 1000001;
        public final static int Endure = 1000002;
        public final static int IronBody = 1001003;
        public final static int PowerStrike = 1001004;
        public final static int SlashBlast = 1001005;
    }
    public final static int[] FighterSkills = {Fighter.SwordMastery, Fighter.AxeMastery, Fighter.FinalAttackSword, Fighter.FinalAttackAxe,
        Fighter.SwordBooster, Fighter.AxeBooster, Fighter.Rage, Fighter.PowerGuard};

    public class Fighter {

        public final static int SwordMastery = 1100000;
        public final static int AxeMastery = 1100001;
        public final static int FinalAttackSword = 1100002;
        public final static int FinalAttackAxe = 1100003;
        public final static int SwordBooster = 1101004;
        public final static int AxeBooster = 1101005;
        public final static int Rage = 1101006;
        public final static int PowerGuard = 1101007;
    }
    public final static int[] CrusaderSkills = {Crusader.ImprovingMPRecovery, Crusader.ShieldMastery, Crusader.ComboAttack, Crusader.SwordPanic,
        Crusader.AxePanic, Crusader.SwordComa, Crusader.AxeComa, Crusader.ArmorCrash, Crusader.Shout};

    public class Crusader {

        public final static int ImprovingMPRecovery = 1110000;
        public final static int ShieldMastery = 1110001;
        public final static int ComboAttack = 1111002;
        public final static int SwordPanic = 1111003;
        public final static int AxePanic = 1111004;
        public final static int SwordComa = 1111005;
        public final static int AxeComa = 1111006;
        public final static int ArmorCrash = 1111007;
        public final static int Shout = 1111008;
    }
    public final static int[] HeroSkills = {Hero.MapleWarrior, Hero.MonsterMagnet, Hero.PowerStance, Hero.AdvancedComboAttack, Hero.Achilles,
        Hero.Guardian, Hero.Rush, Hero.Brandish, Hero.Enrage, Hero.HerosWill};

    public class Hero {

        public final static int MapleWarrior = 1121000;
        public final static int MonsterMagnet = 1121001;
        public final static int PowerStance = 1121002;
        public final static int AdvancedComboAttack = 1120003;
        public final static int Achilles = 1120004;
        public final static int Guardian = 1120005;
        public final static int Rush = 1121006;
        public final static int Brandish = 1121008;
        public final static int Enrage = 1121010;
        public final static int HerosWill = 1121011;
    }
    public final static int[] PageSkills = {Page.SwordMastery, Page.BWMastery, Page.FinalAttackSword, Page.FinalAttackBW,
        Page.SwordBooster, Page.BWBooster, Page.Threaten, Page.PowerGuard};

    public class Page {

        public final static int SwordMastery = 1200000;
        public final static int BWMastery = 1200001;
        public final static int FinalAttackSword = 1200002;
        public final static int FinalAttackBW = 1200003;
        public final static int SwordBooster = 1201004;
        public final static int BWBooster = 1201005;
        public final static int Threaten = 1201006;
        public final static int PowerGuard = 1201007;
    }
    public final static int[] WhiteKnightSkills = {WhiteKnight.ImprovingMPRecovery, WhiteKnight.ShieldMastery, WhiteKnight.ChargedBlow,
        WhiteKnight.FireChargeSword, WhiteKnight.FlameChargeBW, WhiteKnight.IceChargeSword, WhiteKnight.BlizzardChargeBW,
        WhiteKnight.ThunderChargeSword, WhiteKnight.LightningChargeBW, WhiteKnight.MagicCrash};

    public class WhiteKnight {

        public final static int ImprovingMPRecovery = 1210000;
        public final static int ShieldMastery = 1210001;
        public final static int ChargedBlow = 1211002;
        public final static int FireChargeSword = 1211003;
        public final static int FlameChargeBW = 1211004;
        public final static int IceChargeSword = 1211005;
        public final static int BlizzardChargeBW = 1211006;
        public final static int ThunderChargeSword = 1211007;
        public final static int LightningChargeBW = 1211008;
        public final static int MagicCrash = 1211009;
    }
    public final static int[] PaladinSkills = {Paladin.MapleWarrior, Paladin.MonsterMagnet, Paladin.PowerStance, Paladin.HolyChargeSword,
        Paladin.DivineChargeBW, Paladin.Achilles, Paladin.Guardian, Paladin.Rush, Paladin.Blast, Paladin.AdvancedCharge,
        Paladin.HeavensHammer, Paladin.HerosWill};

    public class Paladin {

        public final static int MapleWarrior = 1221000;
        public final static int MonsterMagnet = 1221001;
        public final static int PowerStance = 1221002;
        public final static int HolyChargeSword = 1221003;
        public final static int DivineChargeBW = 1221004;
        public final static int Achilles = 1220005;
        public final static int Guardian = 1220006;
        public final static int Rush = 1221007;
        public final static int Blast = 1221009;
        public final static int AdvancedCharge = 1220010;
        public final static int HeavensHammer = 1221011;
        public final static int HerosWill = 1221012;
    }
    public final static int[] SpearManSkills = {Spearman.SpearMastery, Spearman.PoleArmMastery, Spearman.FinalAttackSpear,
        Spearman.FinalAttackPoleArm, Spearman.SpearBooster, Spearman.PoleArmBooster, Spearman.IronWill, Spearman.HyperBody};

    public class Spearman {

        public final static int SpearMastery = 1300000;
        public final static int PoleArmMastery = 1300001;
        public final static int FinalAttackSpear = 1300002;
        public final static int FinalAttackPoleArm = 1300003;
        public final static int SpearBooster = 1301004;
        public final static int PoleArmBooster = 1301005;
        public final static int IronWill = 1301006;
        public final static int HyperBody = 1301007;
    }
    public final static int[] DragonKnightSkills = {DragonKnight.ElementalResistance, DragonKnight.SpearCrusher,
        DragonKnight.PoleArmCrusher, DragonKnight.DragonFurySpear, DragonKnight.DragonFuryPoleArm, DragonKnight.Sacrifice,
        DragonKnight.DragonRoar, DragonKnight.PowerCrash, DragonKnight.DragonBlood};

    public class DragonKnight {

        public final static int ElementalResistance = 1310000;
        public final static int SpearCrusher = 1311001;
        public final static int PoleArmCrusher = 1311002;
        public final static int DragonFurySpear = 1311003;
        public final static int DragonFuryPoleArm = 1311004;
        public final static int Sacrifice = 1311005;
        public final static int DragonRoar = 1311006;
        public final static int PowerCrash = 1311007;
        public final static int DragonBlood = 1311008;
    }
    public final static int[] DarkKnightSkills = {DarkKnight.MapleWarrior, DarkKnight.MonsterMagnet, DarkKnight.PowerStance,
        DarkKnight.Rush, DarkKnight.Achilles, DarkKnight.Berserk, DarkKnight.Beholder, DarkKnight.AuraOfTheBeholder,
        DarkKnight.HexOfTheBeholder, DarkKnight.HerosWill};

    public class DarkKnight {

        public final static int MapleWarrior = 1321000;
        public final static int MonsterMagnet = 1321001;
        public final static int PowerStance = 1321002;
        public final static int Rush = 1321003;
        public final static int Achilles = 1320005;
        public final static int Berserk = 1320006;
        public final static int Beholder = 1321007;
        public final static int AuraOfTheBeholder = 1320008;
        public final static int HexOfTheBeholder = 1320009;
        public final static int HerosWill = 1321010;
    }
    public final static int[] MagicianSkills = {Magician.ImprovedMPRecovery, Magician.ImprovedMaxMPIncrease, Magician.MagicGuard,
        Magician.MagicArmor, Magician.EnergyBolt, Magician.MagicClaw};

    public class Magician {

        public final static int ImprovedMPRecovery = 2000000;
        public final static int ImprovedMaxMPIncrease = 2000001;
        public final static int MagicGuard = 2001002;
        public final static int MagicArmor = 2001003;
        public final static int EnergyBolt = 2001004;
        public final static int MagicClaw = 2001005;
    }
    public final static int[] FPMagicianSkills = {FPMagician.MPEater, FPMagician.Meditation, FPMagician.Teleport, FPMagician.Slow,
        FPMagician.FireArrow, FPMagician.PoisonBreath};

    public class FPMagician {

        public final static int MPEater = 2100000;
        public final static int Meditation = 2101001;
        public final static int Teleport = 2101002;
        public final static int Slow = 2101003;
        public final static int FireArrow = 2101004;
        public final static int PoisonBreath = 2101005;
    }
    public final static int[] FPMageSkills = {FPMage.PartialResistance, FPMage.ElementAmplification, FPMage.Explosion, FPMage.PoisonMist,
        FPMage.Seal, FPMage.SpellBooster, FPMage.ElementComposition};

    public class FPMage {

        public final static int PartialResistance = 2110000;
        public final static int ElementAmplification = 2110001;
        public final static int Explosion = 2111002;
        public final static int PoisonMist = 2111003;
        public final static int Seal = 2111004;
        public final static int SpellBooster = 2111005;
        public final static int ElementComposition = 2111006;
    }
    public final static int[] FPArchMageSkills = {FPArchMage.MapleWarrior, FPArchMage.BigBang, FPArchMage.ManaReflection,
        FPArchMage.FireDemon, FPArchMage.Infinity, FPArchMage.Elquines, FPArchMage.Paralyze, FPArchMage.MeteorShower, FPArchMage.HerosWill};

    public class FPArchMage {

        public final static int MapleWarrior = 2121000;
        public final static int BigBang = 2121001;
        public final static int ManaReflection = 2121002;
        public final static int FireDemon = 2121003;
        public final static int Infinity = 2121004;
        public final static int Elquines = 2121005;
        public final static int Paralyze = 2121006;
        public final static int MeteorShower = 2121007;
        public final static int HerosWill = 2121008;
    }
    public final static int[] ILMagicianSkills = {ILMagician.MPEater, ILMagician.Meditation, ILMagician.Teleport, ILMagician.Slow,
        ILMagician.ColdBeam, ILMagician.ThunderBolt};

    public class ILMagician {

        public final static int MPEater = 2200000;
        public final static int Meditation = 2201001;
        public final static int Teleport = 2201002;
        public final static int Slow = 2201003;
        public final static int ColdBeam = 2201004;
        public final static int ThunderBolt = 2201005;
    }
    public final static int[] ILMageSkills = {ILMage.PartialResistance, ILMage.ElementAmplification, ILMage.IceStrike,
        ILMage.ThunderSpear, ILMage.Seal, ILMage.SpellBooster, ILMage.ElementComposition};

    public class ILMage {

        public final static int PartialResistance = 2210000;
        public final static int ElementAmplification = 2210001;
        public final static int IceStrike = 2211002;
        public final static int ThunderSpear = 2211003;
        public final static int Seal = 2211004;
        public final static int SpellBooster = 2211005;
        public final static int ElementComposition = 2211006;
    }
    public final static int[] ILArchMageSkills = {ILArchMage.MapleWarrior, ILArchMage.BigBang, ILArchMage.ManaReflection,
        ILArchMage.IceDemon, ILArchMage.Infinity, ILArchMage.Ifrit, ILArchMage.ChainLightning, ILArchMage.Blizzard, ILArchMage.HerosWill};

    public class ILArchMage {

        public final static int MapleWarrior = 2221000;
        public final static int BigBang = 2221001;
        public final static int ManaReflection = 2221002;
        public final static int IceDemon = 2221003;
        public final static int Infinity = 2221004;
        public final static int Ifrit = 2221005;
        public final static int ChainLightning = 2221006;
        public final static int Blizzard = 2221007;
        public final static int HerosWill = 2221008;
    }
    public final static int[] ClericSkills = {Cleric.MPEater, Cleric.Teleport, Cleric.Heal, Cleric.Invincible, Cleric.Bless, Cleric.HolyArrow};

    public class Cleric {

        public final static int MPEater = 2300000;
        public final static int Teleport = 2301001;
        public final static int Heal = 2301002;
        public final static int Invincible = 2301003;
        public final static int Bless = 2301004;
        public final static int HolyArrow = 2301005;
    }
    public final static int[] Priestskills = {Priest.ElementalResistance, Priest.Dispel, Priest.MysticDoor, Priest.HolySymbol,
        Priest.ShiningRay, Priest.Doom, Priest.SummonDragon};

    public class Priest {

        public final static int ElementalResistance = 2310000;
        public final static int Dispel = 2311001;
        public final static int MysticDoor = 2311002;
        public final static int HolySymbol = 2311003;
        public final static int ShiningRay = 2311004;
        public final static int Doom = 2311005;
        public final static int SummonDragon = 2311006;
    }
    public final static int[] BishopSkills = {Bishop.MapleWarrior, Bishop.BigBang, Bishop.ManaReflection, Bishop.Bahamut,
        Bishop.Infinity, Bishop.HolyShield, Bishop.Resurrection, Bishop.AngelRay, Bishop.Genesis, Bishop.HerosWill};

    public class Bishop {

        public final static int MapleWarrior = 2321000;
        public final static int BigBang = 2321001;
        public final static int ManaReflection = 2321002;
        public final static int Bahamut = 2321003;
        public final static int Infinity = 2321004;
        public final static int HolyShield = 2321005;
        public final static int Resurrection = 2321006;
        public final static int AngelRay = 2321007;
        public final static int Genesis = 2321008;
        public final static int HerosWill = 2321009;
    }
    public final static int[] ArcherSkills = {Archer.ArrowBlow, Archer.TheBlessingOfAmazon, Archer.CriticalShot,
        Archer.TheEyeOfAmazon, Archer.Focus, Archer.ArrowBlow, Archer.DoubleShot};

    public class Archer {

        public final static int TheBlessingOfAmazon = 3000000;
        public final static int CriticalShot = 3000001;
        public final static int TheEyeOfAmazon = 3000002;
        public final static int Focus = 3001003;
        public final static int ArrowBlow = 3001004;
        public final static int DoubleShot = 3001005;
    }
    public final static int[] HunterSkills = {Hunter.BowMastery, Hunter.FinalAttack, Hunter.BowBooster,
        Hunter.PowerKnockBack, Hunter.SoulArrow, Hunter.ArrowBomb};

    public class Hunter {

        public final static int BowMastery = 3100000;
        public final static int FinalAttack = 3100001;
        public final static int BowBooster = 3101002;
        public final static int PowerKnockBack = 3101003;
        public final static int SoulArrow = 3101004;
        public final static int ArrowBomb = 3101005;
    }
    public final static int[] RangerSkills = {Ranger.Thrust, Ranger.MortalBlow, Ranger.Puppet, Ranger.Inferno,
        Ranger.ArrowRain, Ranger.SilverHawk, Ranger.Strafe};

    public class Ranger {

        public final static int Thrust = 3110000;
        public final static int MortalBlow = 3110001;
        public final static int Puppet = 3111002;
        public final static int Inferno = 3111003;
        public final static int ArrowRain = 3111004;
        public final static int SilverHawk = 3111005;
        public final static int Strafe = 3111006;
    }
    public final static int[] BowmasterSkills = {Bowmaster.MapleWarrior, Bowmaster.SharpEyes, Bowmaster.DragonsBreath,
        Bowmaster.Hurricane, Bowmaster.BowExpert, Bowmaster.Phoenix, Bowmaster.Hamstring,
        Bowmaster.Concentrate, Bowmaster.HerosWill};

    public class Bowmaster {

        public final static int MapleWarrior = 3121000;
        public final static int SharpEyes = 3121002;
        public final static int DragonsBreath = 3121003;
        public final static int Hurricane = 3121004;
        public final static int BowExpert = 3120005;
        public final static int Phoenix = 3121006;
        public final static int Hamstring = 3121007;
        public final static int Concentrate = 3121008;
        public final static int HerosWill = 3121009;
    }
    public final static int[] CrossBowMankills = {CrossBowMan.CrossbowMastery, CrossBowMan.FinalAttack,
        CrossBowMan.CrossbowBooster, CrossBowMan.PowerKnockBack, CrossBowMan.SoulArrow, CrossBowMan.IronArrow};

    public class CrossBowMan {

        public final static int CrossbowMastery = 3200000;
        public final static int FinalAttack = 3200001;
        public final static int CrossbowBooster = 3201002;
        public final static int PowerKnockBack = 3201003;
        public final static int SoulArrow = 3201004;
        public final static int IronArrow = 3201005;
    }
    public final static int[] SniperSkills = {Sniper.Thrust, Sniper.MortalBlow, Sniper.Puppet, Sniper.Blizzard,
        Sniper.ArrowEruption, Sniper.GoldenEagle, Sniper.Strafe};

    public class Sniper {

        public final static int Thrust = 3210000;
        public final static int MortalBlow = 3210001;
        public final static int Puppet = 3211002;
        public final static int Blizzard = 3211003;
        public final static int ArrowEruption = 3211004;
        public final static int GoldenEagle = 3211005;
        public final static int Strafe = 3211006;
    }
    public final static int[] MarksmanSkills = {Marksman.MapleWarrior, Marksman.PiercingArrow, Marksman.SharpEyes,
        Marksman.DragonsBreath, Marksman.MarksmanBoost, Marksman.Frostprey, Marksman.Snipe, Marksman.Blind, Marksman.HerosWill};

    public class Marksman {

        public final static int MapleWarrior = 3221000;
        public final static int PiercingArrow = 3221001;
        public final static int SharpEyes = 3221002;
        public final static int DragonsBreath = 3221003;
        public final static int MarksmanBoost = 3220004;
        public final static int Frostprey = 3221005;
        public final static int Blind = 3221006;
        public final static int Snipe = 3221007;
        public final static int HerosWill = 3221008;
    }
    public final static int[] RogueSkills = {Rogue.NimbleBody, Rogue.KeenEyes, Rogue.Disorder, Rogue.DarkSight,
        Rogue.DoubleStab, Rogue.LuckySeven};

    public class Rogue {

        public final static int NimbleBody = 4000000;
        public final static int KeenEyes = 4000001;
        public final static int Disorder = 4001002;
        public final static int DarkSight = 4001003;
        public final static int DoubleStab = 4001334;
        public final static int LuckySeven = 4001344;
    }
    public final static int[] AssassinSkills = {Assassin.ClawMastery, Assassin.CriticalThrow, Assassin.Endure,
        Assassin.ClawBooster, Assassin.Haste, Assassin.Drain};

    public class Assassin {

        public final static int ClawMastery = 4100000;
        public final static int CriticalThrow = 4100001;
        public final static int Endure = 4100002;
        public final static int ClawBooster = 4101003;
        public final static int Haste = 4101004;
        public final static int Drain = 4101005;
    }
    public final static int[] HermitSkills = {Hermit.Alchemist, Hermit.MesoUp, Hermit.ShadowPartner,
        Hermit.ShadowWeb, Hermit.ShadowMeso, Hermit.Avenger, Hermit.FlashJump};

    public class Hermit {

        public final static int Alchemist = 4110000;
        public final static int MesoUp = 4111001;
        public final static int ShadowPartner = 4111002;
        public final static int ShadowWeb = 4111003;
        public final static int ShadowMeso = 4111004;
        public final static int Avenger = 4111005;
        public final static int FlashJump = 4111006;
    }
    public final static int[] NightLordSkills = {NightLord.MapleWarrior, NightLord.ShadowShifter, NightLord.Taunt,
        NightLord.NinjaAmbush, NightLord.VenomousStar, NightLord.ShadowStars, NightLord.TripleThrow,
        NightLord.NinjaStorm, NightLord.HerosWill};

    public class NightLord {

        public final static int MapleWarrior = 4121000;
        public final static int ShadowShifter = 4120002;
        public final static int Taunt = 4121003;
        public final static int NinjaAmbush = 4121004;
        public final static int VenomousStar = 4120005;
        public final static int ShadowStars = 4121006;
        public final static int TripleThrow = 4121007;
        public final static int NinjaStorm = 4121008;
        public final static int HerosWill = 4121009;
    }
    public final static int[] BanditSkills = {Bandit.DaggerMastery, Bandit.Endure, Bandit.DaggerBooster,
        Bandit.Haste, Bandit.Steal, Bandit.SavageBlow};

    public class Bandit {

        public final static int DaggerMastery = 4200000;
        public final static int Endure = 4200001;
        public final static int DaggerBooster = 4201002;
        public final static int Haste = 4201003;
        public final static int Steal = 4201004;
        public final static int SavageBlow = 4201005;
    }
    public final static int[] ChiefBanditSkills = {ChiefBandit.ShieldMastery, ChiefBandit.Chakra, ChiefBandit.Assaulter,
        ChiefBandit.Pickpocket, ChiefBandit.BandofThieves, ChiefBandit.MesoGuard, ChiefBandit.MesoExplosion};

    public class ChiefBandit {

        public final static int ShieldMastery = 4210000;
        public final static int Chakra = 4211001;
        public final static int Assaulter = 4211002;
        public final static int Pickpocket = 4211003;
        public final static int BandofThieves = 4211004;
        public final static int MesoGuard = 4211005;
        public final static int MesoExplosion = 4211006;
    }
    public final static int[] ShadowerSkills = {Shadower.MapleWarrior, Shadower.Assassinate, Shadower.ShadowShifter,
        Shadower.Taunt, Shadower.NinjaAmbush, Shadower.VenomousStab, Shadower.Smokescreen,
        Shadower.BoomerangStep, Shadower.HerosWill};

    public class Shadower {

        public final static int MapleWarrior = 4221000;
        public final static int Assassinate = 4221001;
        public final static int ShadowShifter = 4220002;
        public final static int Taunt = 4221003;
        public final static int NinjaAmbush = 4221004;
        public final static int VenomousStab = 4220005;
        public final static int Smokescreen = 4221006;
        public final static int BoomerangStep = 4221007;
        public final static int HerosWill = 4221008;
    }
    public final static int[] PirateSkills = {Pirate.BulletTime, Pirate.FlashFist, Pirate.SommersaultKick, Pirate.DoubleShot};

    public class Pirate {

        public final static int BulletTime = 5000000;
        public final static int FlashFist = 5001001;
        public final static int SommersaultKick = 5001002;
        public final static int DoubleShot = 5001003;
        public final static int Dash = 5001005;
    }
    public final static int[] BrawlerSkills = {Brawler.ImproveMaxHP, Brawler.KnucklerMastery, Brawler.BackspinBlow,
        Brawler.CorkscrewBlow, Brawler.MPRecovery, Brawler.KnucklerBooster, Brawler.OakBarrel};

    public class Brawler {

        public final static int ImproveMaxHP = 5100000;
        public final static int KnucklerMastery = 5100001;
        public final static int BackspinBlow = 5101002;
        public final static int DoubleUppercut = 5101003;
        public final static int CorkscrewBlow = 5101004;
        public final static int MPRecovery = 5101005;
        public final static int KnucklerBooster = 5101006;
        public final static int OakBarrel = 5101007;
    }
    public final static int[] MarauderSkills = {Marauder.StunMastery, Marauder.EnergyCharge, Marauder.EnergyBlast,
        Marauder.EnergyDrain, Marauder.Transformation, Marauder.Shockwave};

    public class Marauder {

        public final static int StunMastery = 5110000;
        public final static int EnergyCharge = 5110001;
        public final static int EnergyBlast = 5111002;
        public final static int EnergyDrain = 5111004;
        public final static int Transformation = 5111005;
        public final static int Shockwave = 5111006;
    }
    public final static int[] BuccaneerSkills = {Buccaneer.MapleWarrior, Buccaneer.DragonStrike, Buccaneer.EnergyOrb,
        Buccaneer.SuperTransformation, Buccaneer.Demolition, Buccaneer.Snatch, Buccaneer.Barrage,
        Buccaneer.PiratesRage, Buccaneer.SpeedInfusion, Buccaneer.TimeLeap};

    public class Buccaneer {

        public final static int MapleWarrior = 5121000;
        public final static int DragonStrike = 5121001;
        public final static int EnergyOrb = 5121002;
        public final static int SuperTransformation = 5121003;
        public final static int Demolition = 5121004;
        public final static int Snatch = 5121005;
        public final static int Barrage = 5121007;
        public final static int PiratesRage = 5121008;
        public final static int SpeedInfusion = 5121009;
        public final static int TimeLeap = 5121010;
    }
    public final static int[] GunslingerSkills = {Gunslinger.GunMastery, Gunslinger.InvisibleShot,
        Gunslinger.Grenade, Gunslinger.GunBooster, Gunslinger.BlankShot, Gunslinger.Wings, Gunslinger.RecoilShot};

    public class Gunslinger {

        public final static int GunMastery = 5200000;
        public final static int InvisibleShot = 5201001;
        public final static int Grenade = 5201002;
        public final static int GunBooster = 5201003;
        public final static int BlankShot = 5201004;
        public final static int Wings = 5201005;
        public final static int RecoilShot = 5201006;
    }
    public final static int[] OutlawSkills = {Outlaw.BurstFire, Outlaw.Octopus, Outlaw.Gaviota, Outlaw.Flamethrower,
        Outlaw.IceSplitter, Outlaw.HomingBeacon};

    public class Outlaw {

        public final static int BurstFire = 5210000;
        public final static int Octopus = 5211001;
        public final static int Gaviota = 5211002;
        public final static int Flamethrower = 5211004;
        public final static int IceSplitter = 5211005;
        public final static int HomingBeacon = 5211006;
    }
    public final static int[] CorsairSkills = {Corsair.MapleWarrior, Corsair.ElementalBoost, Corsair.WrathOfTheOctopi,
        Corsair.AerialStrike, Corsair.RapidFire, Corsair.Battleship, Corsair.BattleshipCannon,
        Corsair.BattleshipTorpedo, Corsair.Hypnotize, Corsair.SpeedInfusion, Corsair.Bullseye};

    public class Corsair {

        public final static int MapleWarrior = 5221000;
        public final static int ElementalBoost = 5220001;
        public final static int WrathOfTheOctopi = 5220002;
        public final static int AerialStrike = 5221003;
        public final static int RapidFire = 5221004;
        public final static int Battleship = 5221006;
        public final static int BattleshipCannon = 5221007;
        public final static int BattleshipTorpedo = 5221008;
        public final static int Hypnotize = 5221009;
        public final static int SpeedInfusion = 5221010;
        public final static int Bullseye = 5220011;
    }
    public final static int[] NoblesseSkills = {Noblesse.BlessingOfTheFairy, Noblesse.FollowTheLead,
        Noblesse.ThreeSnails, Noblesse.Recovery, Noblesse.NimbleFeet, Noblesse.LegendarySpirit, Noblesse.MonsterRider,
        Noblesse.EchoOfHero, Noblesse.JumpDown, Noblesse.Maker, Noblesse.BambooThrust, Noblesse.InvincibleBarrier, Noblesse.MeteoShower};

    public class Noblesse {

        public final static int BlessingOfTheFairy = 10000012;
        public final static int FollowTheLead = 10000018;
        public final static int ThreeSnails = 10001000;
        public final static int Recovery = 10001001;
        public final static int NimbleFeet = 10001002;
        public final static int LegendarySpirit = 10001003;
        public final static int MonsterRider = 10001004;
        public final static int EchoOfHero = 10001005;
        public final static int JumpDown = 10001006;
        public final static int Maker = 10001007;
        public final static int BambooThrust = 10001009;
        public final static int InvincibleBarrier = 10001010;
        public final static int MeteoShower = 10001011;
    }
    public final static int[] DawnWarrior1Skills = {DawnWarrior1.MaxHPEnhancement, DawnWarrior1.IronBody,
        DawnWarrior1.PowerStrike, DawnWarrior1.SlashBlast, DawnWarrior1.Soul};

    public class DawnWarrior1 {

        public final static int MaxHPEnhancement = 11000000;
        public final static int IronBody = 11001001;
        public final static int PowerStrike = 11001002;
        public final static int SlashBlast = 11001003;
        public final static int Soul = 11001004;
    }
    public final static int[] DawnWarrior2Skills = {DawnWarrior2.SwordMastery, DawnWarrior2.SwordBooster,
        DawnWarrior2.FinalAttack, DawnWarrior2.Rage, DawnWarrior2.SoulBlade, DawnWarrior2.SoulRush};

    public class DawnWarrior2 {

        public final static int SwordMastery = 11100000;
        public final static int SwordBooster = 11101001;
        public final static int FinalAttack = 11101002;
        public final static int Rage = 11101003;
        public final static int SoulBlade = 11101004;
        public final static int SoulRush = 11101005;
    }
    public final static int[] DawnWarrior3skills = {DawnWarrior3.MPRecoveryRateEnhancement, DawnWarrior3.ComboAttack, DawnWarrior3.Panic,
        DawnWarrior3.Coma, DawnWarrior3.Brandish, DawnWarrior3.Advancedcombo, DawnWarrior3.SoulDriver, DawnWarrior3.SoulCharge};

    public class DawnWarrior3 {

        public final static int MPRecoveryRateEnhancement = 11110000;
        public final static int ComboAttack = 11111001;
        public final static int Panic = 11111002;
        public final static int Coma = 11111003;
        public final static int Brandish = 11111004;
        public final static int Advancedcombo = 11110005;
        public final static int SoulDriver = 11111006;
        public final static int SoulCharge = 11111007;
    }
    public final static int[] BlazeWizard1Skills = {BlazeWizard1.IncreasingMaxMP, BlazeWizard1.MagicGuard,
        BlazeWizard1.MagicArmor, BlazeWizard1.MagicClaw, BlazeWizard1.Flame};

    public class BlazeWizard1 {

        public final static int IncreasingMaxMP = 12000000;
        public final static int MagicGuard = 12001001;
        public final static int MagicArmor = 12001002;
        public final static int MagicClaw = 12001003;
        public final static int Flame = 12001004;
    }
    public final static int[] BlazeWizard2Skills = {BlazeWizard2.Meditation, BlazeWizard2.Slow, BlazeWizard2.FireArrow,
        BlazeWizard2.Teleport, BlazeWizard2.SpellBooster, BlazeWizard2.ElementalReset, BlazeWizard2.FirePillar};

    public class BlazeWizard2 {

        public final static int Meditation = 12101000;
        public final static int Slow = 12101001;
        public final static int FireArrow = 12101002;
        public final static int Teleport = 12101003;
        public final static int SpellBooster = 12101004;
        public final static int ElementalReset = 12101005;
        public final static int FirePillar = 12101006;
    }
    public final static int[] BlazeWizard3Skills = {BlazeWizard3.ElementalResistance, BlazeWizard3.ElementAmplification,
        BlazeWizard3.Seal, BlazeWizard3.MeteorShower, BlazeWizard3.Ifrit, BlazeWizard3.FlameGear, BlazeWizard3.FireStrike};

    public class BlazeWizard3 {

        public final static int ElementalResistance = 12110000;
        public final static int ElementAmplification = 12110001;
        public final static int Seal = 12111002;
        public final static int MeteorShower = 12111003;
        public final static int Ifrit = 12111004;
        public final static int FlameGear = 12111005;
        public final static int FireStrike = 12111006;
    }
    public final static int[] WindArcher1Skills = {WindArcher1.CriticalShot, WindArcher1.TheEyeofAmazon,
        WindArcher1.Focus, WindArcher1.DoubleShot, WindArcher1.Storm};

    public class WindArcher1 {

        public final static int CriticalShot = 13000000;
        public final static int TheEyeofAmazon = 13000001;
        public final static int Focus = 13001002;
        public final static int DoubleShot = 13001003;
        public final static int Storm = 13001004;
    }
    public final static int[] WindArcher2Skills = {WindArcher2.BowMastery, WindArcher2.BowBooster,
        WindArcher2.FinalAttack, WindArcher2.SoulArrow, WindArcher2.Thrust, WindArcher2.StormBreak, WindArcher2.WindWalk};

    public class WindArcher2 {

        public final static int BowMastery = 13100000;
        public final static int BowBooster = 13101001;
        public final static int FinalAttack = 13101002;
        public final static int SoulArrow = 13101003;
        public final static int Thrust = 13100004;
        public final static int StormBreak = 13101005;
        public final static int WindWalk = 13101006;
    }
    public final static int[] WindArcher3Skills = {WindArcher3.ArrowRain, WindArcher3.Strafe, WindArcher3.Hurricane,
        WindArcher3.BowExpert, WindArcher3.Puppet, WindArcher3.EagleEye, WindArcher3.WindPiercing, WindArcher3.WindShot};

    public class WindArcher3 {

        public final static int ArrowRain = 13111000;
        public final static int Strafe = 13111001;
        public final static int Hurricane = 13111002;
        public final static int BowExpert = 13110003;
        public final static int Puppet = 13111004;
        public final static int EagleEye = 13111005;
        public final static int WindPiercing = 13111006;
        public final static int WindShot = 13111007;
    }
    public final static int[] NightWalker1Skills = {NightWalker1.NimbleBody, NightWalker1.KeenEyes,
        NightWalker1.Disorder, NightWalker1.DarkSight, NightWalker1.LuckySeven, NightWalker1.Darkness};

    public class NightWalker1 {

        public final static int NimbleBody = 14000000;
        public final static int KeenEyes = 14000001;
        public final static int Disorder = 14001002;
        public final static int DarkSight = 14001003;
        public final static int LuckySeven = 14001004;
        public final static int Darkness = 14001005;
    }
    public final static int[] NightWalker2Skills = {NightWalker2.ClawMastery, NightWalker2.CriticalThrow,
        NightWalker2.ClawBooster, NightWalker2.Haste, NightWalker2.FlashJump, /*NightWalker2.Vanish,*/ NightWalker2.Vampire};

    public class NightWalker2 {

        public final static int ClawMastery = 14100000;
        public final static int CriticalThrow = 14100001;
        public final static int ClawBooster = 14101002;
        public final static int Haste = 14101003;
        public final static int FlashJump = 14101004;
        public final static int Vanish = 14100005;
        public final static int Vampire = 14101006;
    }
    public final static int[] NightWalker3Skills = {NightWalker3.ShadowPartner, NightWalker3.ShadowWeb,
        NightWalker3.Avenger, NightWalker3.Alchemist, NightWalker3.Venom, NightWalker3.Triplethrow, NightWalker3.PoisonBomb};

    public class NightWalker3 {

        public final static int ShadowPartner = 14111000;
        public final static int ShadowWeb = 14111001;
        public final static int Avenger = 14111002;
        public final static int Alchemist = 14110003;
        public final static int Venom = 14110004;
        public final static int Triplethrow = 14111005;
        public final static int PoisonBomb = 14111006;
    }
    public final static int[] ThunderBreaker1Skills = {ThunderBreaker1.QuickMotion, ThunderBreaker1.Straight,
        ThunderBreaker1.SomersaultKick, ThunderBreaker1.Lightning};

    public class ThunderBreaker1 {

        public final static int QuickMotion = 15000000;
        public final static int Straight = 15001001;
        public final static int SomersaultKick = 15001002;
        public final static int Dash = 15001003;
        public final static int Lightning = 15001004;
    }
    public final static int[] ThunderBreaker2Skills = {ThunderBreaker2.ImproveMaxHP, ThunderBreaker2.KnuckleMastery,
        ThunderBreaker2.KnuckleBooster, ThunderBreaker2.CorkscrewBlow, ThunderBreaker2.EnergyCharge,
        ThunderBreaker2.EnergyBlast, ThunderBreaker2.LightningCharge};

    public class ThunderBreaker2 {

        public final static int ImproveMaxHP = 15100000;
        public final static int KnuckleMastery = 15100001;
        public final static int KnuckleBooster = 15101002;
        public final static int CorkscrewBlow = 15101003;
        public final static int EnergyCharge = 15100004;
        public final static int EnergyBlast = 15101005;
        public final static int LightningCharge = 15101006;
    }
    public final static int[] ThunderBreaker3Skills = {ThunderBreaker3.CriticalPunch, ThunderBreaker3.EnergyDrain,
        ThunderBreaker3.Transformation, ThunderBreaker3.Shockwave, ThunderBreaker3.Barrage, ThunderBreaker3.SpeedInfusion,
        ThunderBreaker3.Spark, ThunderBreaker3.SharkWave};

    public class ThunderBreaker3 {

        public final static int CriticalPunch = 15110000;
        public final static int EnergyDrain = 15111001;
        public final static int Transformation = 15111002;
        public final static int Shockwave = 15111003;
        public final static int Barrage = 15111004;
        public final static int SpeedInfusion = 15111005;
        public final static int Spark = 15111006;
        public final static int SharkWave = 15111007;
    }

    public class MapleBrigadier {

        public final static int Skill1 = 8001000;
        public final static int Skill2 = 8001001;
    }
    public final static int[] GMSkills = {GM.Haste, GM.SuperDragonRoar, GM.Teleport};

    public class GM {

        public final static int Haste = 9001000;
        public final static int SuperDragonRoar = 9001001;
        public final static int Teleport = 9001002;
    }
    public final static int[] SuperGMSkills = {SuperGM.HealDispel, SuperGM.Haste, SuperGM.HolySymbol, SuperGM.Bless,
    SuperGM.Hide, SuperGM.Resurrection, SuperGM.HyperBody};

    public class SuperGM {

        public final static int HealDispel = 9101000;
        public final static int Haste = 9101001;
        public final static int HolySymbol = 9101002;
        public final static int Bless = 9101003;
        public final static int Hide = 9101004;
        public final static int Resurrection = 9101005;
        public final static int SuperDragonRoar = 9101006;
        public final static int Teleport = 9101007;
        public final static int HyperBody = 9101008;
    }
}
