USE [master]
GO
/****** Object:  Database [DigitalDevice]    Script Date: 3/16/2025 1:06:42 AM ******/
CREATE DATABASE [DigitalDevice]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'DigitalDevice', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\DigitalDevice.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'DigitalDevice_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\DigitalDevice_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [DigitalDevice] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [DigitalDevice].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [DigitalDevice] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [DigitalDevice] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [DigitalDevice] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [DigitalDevice] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [DigitalDevice] SET ARITHABORT OFF 
GO
ALTER DATABASE [DigitalDevice] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [DigitalDevice] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [DigitalDevice] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [DigitalDevice] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [DigitalDevice] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [DigitalDevice] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [DigitalDevice] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [DigitalDevice] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [DigitalDevice] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [DigitalDevice] SET  ENABLE_BROKER 
GO
ALTER DATABASE [DigitalDevice] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [DigitalDevice] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [DigitalDevice] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [DigitalDevice] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [DigitalDevice] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [DigitalDevice] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [DigitalDevice] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [DigitalDevice] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [DigitalDevice] SET  MULTI_USER 
GO
ALTER DATABASE [DigitalDevice] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [DigitalDevice] SET DB_CHAINING OFF 
GO
ALTER DATABASE [DigitalDevice] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [DigitalDevice] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [DigitalDevice] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [DigitalDevice] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [DigitalDevice] SET QUERY_STORE = ON
GO
ALTER DATABASE [DigitalDevice] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [DigitalDevice]
GO
/****** Object:  Table [dbo].[Authority]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Authority](
	[AuthorityID] [varchar](50) NOT NULL,
	[NameAuthority] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[AuthorityID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CategoryDevice]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CategoryDevice](
	[CategoryDeviceID] [varchar](50) NOT NULL,
	[Description] [nvarchar](255) NULL,
	[PhotoPath] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[CategoryDeviceID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Device]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Device](
	[DeviceID] [varchar](50) NOT NULL,
	[RoomID] [varchar](50) NULL,
	[NameDevice] [nvarchar](100) NOT NULL,
	[PhotoPath] [nvarchar](255) NULL,
	[CategoryDeviceID] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[DeviceID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeviceFunction]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeviceFunction](
	[DeviceFunctionID] [varchar](50) NOT NULL,
	[DeviceID] [varchar](50) NULL,
	[FunctionID] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[DeviceFunctionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeviceFunctionality]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeviceFunctionality](
	[FunctionID] [varchar](50) NOT NULL,
	[Description] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[FunctionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[History]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[History](
	[HistoryID] [varchar](50) NOT NULL,
	[Action] [nvarchar](255) NULL,
	[Time] [datetime] NOT NULL,
	[UserID] [varchar](50) NULL,
	[DeviceID] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[HistoryID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Home]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Home](
	[HomeID] [varchar](50) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Address] [nvarchar](255) NULL,
	[PhotoPath] [nvarchar](255) NULL,
	[UrlMQTT] [nvarchar](255) NULL,
	[UserMQTT] [nvarchar](100) NULL,
	[PasswordMQTT] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[HomeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HomeUser]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HomeUser](
	[HomeUserID] [varchar](50) NOT NULL,
	[UserID] [varchar](50) NULL,
	[HomeID] [varchar](50) NULL,
	[Description] [nvarchar](255) NULL,
	[AuthorityID] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[HomeUserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Room]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Room](
	[RoomID] [varchar](50) NOT NULL,
	[HomeID] [varchar](50) NULL,
	[Name] [nvarchar](100) NOT NULL,
	[PhotoPath] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[RoomID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 3/16/2025 1:06:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [varchar](50) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Password] [nvarchar](100) NOT NULL,
	[Phone] [nvarchar](20) NULL,
	[PhotoPath] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[Authority] ([AuthorityID], [NameAuthority], [Description]) VALUES (N'Auth_01', N'Home Owner', NULL)
INSERT [dbo].[Authority] ([AuthorityID], [NameAuthority], [Description]) VALUES (N'Auth_02', N'Member', NULL)
GO
INSERT [dbo].[CategoryDevice] ([CategoryDeviceID], [Description], [PhotoPath]) VALUES (N'cat_aircon', N'Cooling and air conditioning systems.', N'/images/category_aircon.jpg')
INSERT [dbo].[CategoryDevice] ([CategoryDeviceID], [Description], [PhotoPath]) VALUES (N'cat_device', N'Miscellaneous smart home devices.', N'/images/category_device.jpg')
INSERT [dbo].[CategoryDevice] ([CategoryDeviceID], [Description], [PhotoPath]) VALUES (N'cat_lamp', N'Lighting devices such as LED and smart lamps.', N'/images/category_lamp.jpg')
INSERT [dbo].[CategoryDevice] ([CategoryDeviceID], [Description], [PhotoPath]) VALUES (N'cat_vehicle', N'vehicle in home', NULL)
GO
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_001', N'r_001', N'Lamp - 1 ', NULL, N'cat_lamp')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_002', N'r_002', N'Lamp - 1', NULL, N'cat_lamp')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_003', N'r_003', N'Lamp - 1', NULL, N'cat_lamp')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_004', N'r_001', N'Air Conditioner', NULL, N'cat_aircon')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_005', N'r_004', N'Water Heater', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_006', N'r_004', N'Lamp', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_007', N'r_005', N'Lamp', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_010', N'r_002', N'Bedroom Lamp 2', NULL, N'cat_lamp')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_011', N'r_002', N'Smart Switch 1', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_012', N'r_001', N'Switch - 1', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_013', N'r_001', N'Switch - 2', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_014', N'r_002', N'Smart Switch 2', NULL, N'cat_device')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_015', N'r_001', N'Lamp - 2', NULL, NULL)
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_016', N'vehicle_001', N'Air Blade', N'/images/vehicle_ab.png', N'cat_vehicle')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_017', N'vehicle_001', N'Vision', NULL, N'cat_vehicle')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_18', N'vehicle_001', N'Vios', NULL, N'cat_vehicle')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_20', N'vehicle_001', N'Santafe', NULL, N'cat_vehicle')
INSERT [dbo].[Device] ([DeviceID], [RoomID], [NameDevice], [PhotoPath], [CategoryDeviceID]) VALUES (N'd_21', N'vehicle_001', N'Camry', NULL, N'cat_vehicle')
GO
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_001', N'd_001', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_002', N'd_002', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_003', N'd_003', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_004', N'd_004', N'F_Air')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_005', N'd_005', N'F_WaterHeater')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_006', N'd_006', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_007', N'd_007', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_010', N'd_010', N'F_Lamp')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_011', N'd_011', N'F_Different')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_012', N'd_012', N'F_Different')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_013', N'd_013', N'F_Different')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_014', N'd_014', N'F_Different')
INSERT [dbo].[DeviceFunction] ([DeviceFunctionID], [DeviceID], [FunctionID]) VALUES (N'df_15', N'd_015', N'F_Lamp')
GO
INSERT [dbo].[DeviceFunctionality] ([FunctionID], [Description]) VALUES (N'F_Air', N'Air conditioner')
INSERT [dbo].[DeviceFunctionality] ([FunctionID], [Description]) VALUES (N'F_Different', N'Different Device')
INSERT [dbo].[DeviceFunctionality] ([FunctionID], [Description]) VALUES (N'F_lamp', N'Turn On/Off Lamp')
INSERT [dbo].[DeviceFunctionality] ([FunctionID], [Description]) VALUES (N'F_Vehicle', N'Vehicle')
INSERT [dbo].[DeviceFunctionality] ([FunctionID], [Description]) VALUES (N'F_WaterHeater', N'Water heater')
GO
INSERT [dbo].[Home] ([HomeID], [Name], [Address], [PhotoPath], [UrlMQTT], [UserMQTT], [PasswordMQTT]) VALUES (N'h_41924871', N'Nhà Của Dũng', N'Lạng Giang , Bắc Giang', NULL, N'bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud', N'hivemq.webclient.1734466985272', N'3#2NHU;F.GRih5s6tqe&')
INSERT [dbo].[Home] ([HomeID], [Name], [Address], [PhotoPath], [UrlMQTT], [UserMQTT], [PasswordMQTT]) VALUES (N'h_41943842', N'Nhà Minh', N'Hoàng Quốc Việt, Cầu Giấy', NULL, N'bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud', N'NULhivemq.webclient.1734466985272L', N'3#2NHU;F.GRih5s6tqe&')
INSERT [dbo].[Home] ([HomeID], [Name], [Address], [PhotoPath], [UrlMQTT], [UserMQTT], [PasswordMQTT]) VALUES (N'h_41943843', N'Vila Hiền', N'Cầu Giấy, Hà Nội', NULL, N'bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud', N'NULhivemq.webclient.1734466985272L', N'3#2NHU;F.GRih5s6tqe&')
INSERT [dbo].[Home] ([HomeID], [Name], [Address], [PhotoPath], [UrlMQTT], [UserMQTT], [PasswordMQTT]) VALUES (N'h_41943852', N'Nhà Của Châu', N'Gia Viễn Ninh Bình', NULL, N'bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud', N'hivemq.webclient.1734466985272', N'3#2NHU;F.GRih5s6tqe&')
GO
INSERT [dbo].[HomeUser] ([HomeUserID], [UserID], [HomeID], [Description], [AuthorityID]) VALUES (N'HU_MSSDAD', N'UAMUPFJRB', N'h_41943852', NULL, N'Auth_01')
INSERT [dbo].[HomeUser] ([HomeUserID], [UserID], [HomeID], [Description], [AuthorityID]) VALUES (N'HU_MSSDDC', N'UAMUPFJRB', N'h_41943843', NULL, N'Auth_01')
INSERT [dbo].[HomeUser] ([HomeUserID], [UserID], [HomeID], [Description], [AuthorityID]) VALUES (N'HU_MUPFDDC', N'UAMUPFJRB', N'h_41943842', NULL, N'Auth_01')
INSERT [dbo].[HomeUser] ([HomeUserID], [UserID], [HomeID], [Description], [AuthorityID]) VALUES (N'HU_MUPFJRB', N'UAMUPFJRB', N'h_41924871', NULL, N'Auth_01')
GO
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_001', N'h_41924871', N'LivingRoom', N'/images/icon_livingroom.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_002', N'h_41924871', N'BadRoom', N'/images/icon_badroom.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_003', N'h_41924871', N'Kitchen', N'/images/icon_kitchen.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_004', N'h_41924871', N'BathRoom', N'/images/icon_bathroom.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_005', N'h_41924871', N'StudyRoom', N'/images/icon_different.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_006', N'h_41924871', N'BathRoom2', N'/images/icon_bathroom.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_007', N'h_41924871', N'BadRoom2', N'/images/icon_badroom.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'r_008', N'h_41924871', N'Kitchen2', N'/images/icon_kitchen.png')
INSERT [dbo].[Room] ([RoomID], [HomeID], [Name], [PhotoPath]) VALUES (N'vehicle_001', N'h_41924871', N'Vehicle', NULL)
GO
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'U1', N'John Doe', N'password123', N'0912345678', N'photos/johndoe.jpg')
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'U2', N'Jane Smith', N'mypassword', N'0987654321', N'photos/janesmith.jpg')
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'U3', N'Alice Brown', N'alice123', N'0922334455', N'photos/alicebrown.jpg')
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'U4', N'Bob Johnson', N'securepass', N'0966778899', N'photos/bobjohnson.jpg')
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'U5', N'Charlie White', N'charliepw', N'0933445566', N'photos/charliewhite.jpg')
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'UAMUPFJRB', N'dung', N'111', N'0961848526', NULL)
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'UD8FI6TQY', N'Nguyễn Đức Dũng', N'sss', N'0961848526', NULL)
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'US4HM5REG', N'Châu', N'111', N'+84961848526', NULL)
INSERT [dbo].[Users] ([UserID], [Name], [Password], [Phone], [PhotoPath]) VALUES (N'UVJZBTV0Y', N'Hiền', N'111', N'9999999999', NULL)
GO
ALTER TABLE [dbo].[Device]  WITH CHECK ADD FOREIGN KEY([CategoryDeviceID])
REFERENCES [dbo].[CategoryDevice] ([CategoryDeviceID])
GO
ALTER TABLE [dbo].[Device]  WITH CHECK ADD FOREIGN KEY([RoomID])
REFERENCES [dbo].[Room] ([RoomID])
GO
ALTER TABLE [dbo].[DeviceFunction]  WITH CHECK ADD FOREIGN KEY([DeviceID])
REFERENCES [dbo].[Device] ([DeviceID])
GO
ALTER TABLE [dbo].[DeviceFunction]  WITH CHECK ADD FOREIGN KEY([FunctionID])
REFERENCES [dbo].[DeviceFunctionality] ([FunctionID])
GO
ALTER TABLE [dbo].[History]  WITH CHECK ADD FOREIGN KEY([DeviceID])
REFERENCES [dbo].[Device] ([DeviceID])
GO
ALTER TABLE [dbo].[History]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[HomeUser]  WITH CHECK ADD FOREIGN KEY([AuthorityID])
REFERENCES [dbo].[Authority] ([AuthorityID])
GO
ALTER TABLE [dbo].[HomeUser]  WITH CHECK ADD FOREIGN KEY([HomeID])
REFERENCES [dbo].[Home] ([HomeID])
GO
ALTER TABLE [dbo].[HomeUser]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Room]  WITH CHECK ADD FOREIGN KEY([HomeID])
REFERENCES [dbo].[Home] ([HomeID])
GO
USE [master]
GO
ALTER DATABASE [DigitalDevice] SET  READ_WRITE 
GO
