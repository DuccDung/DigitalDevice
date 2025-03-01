﻿using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class DevicesController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		private object random;

	
		public DevicesController(DigitalDeviceContext digitalDeviceContext)
		{
			_digitalDeviceContext = digitalDeviceContext;
		}

		[HttpPost]
		[Route("CreateDevice")]

		public IActionResult CreateDevice(string DeviceId, string Name, string RoomId, string FunctionId)
		{
			var isDevice = _digitalDeviceContext.Devices.Find(DeviceId);
			var isRoom = _digitalDeviceContext.Rooms.Find(RoomId);
			var isFunction = _digitalDeviceContext.DeviceFunctionalities.Find(FunctionId);

			if (isDevice != null || isFunction == null || isRoom == null)
			{
				return BadRequest("Device existed or the room and function do not exist");
			}

			// Tạo thiết bị mới
			Device device = new Device
			{
				DeviceId = DeviceId,
				NameDevice = Name,
				RoomId = RoomId
			};

			// Lấy danh sách các ID hiện có
			var existingIDs = _digitalDeviceContext.DeviceFunctions
				.Select(df => df.DeviceFunctionId)
				.ToList();

			// Tạo ID ngẫu nhiên không trùng
			string newFunctionId = GenerateRandomDeviceFunctionID(existingIDs);

			DeviceFunction deviceFunction = new DeviceFunction
			{
				DeviceFunctionId = newFunctionId, // Gán ID mới
				DeviceId = DeviceId,
				FunctionId = FunctionId
			};

			_digitalDeviceContext.Devices.Add(device);
			_digitalDeviceContext.DeviceFunctions.Add(deviceFunction);
			_digitalDeviceContext.SaveChanges();

			return Ok(new { DeviceId = DeviceId, FunctionId = newFunctionId });
		}

		// Hàm sinh ID ngẫu nhiên
		private string GenerateRandomDeviceFunctionID(List<string> existingIDs)
		{
			Random random = new Random();
			string newID;
			do
			{
				int randomNumber = random.Next(1, 1000); // Tạo số ngẫu nhiên từ 1-999
				newID = $"df_{randomNumber:D3}";
			}
			while (existingIDs.Contains(newID)); // Kiểm tra trùng

			return newID;
		}



		[HttpGet]
		[Route("GetDevicesByRoomID")]
		public IActionResult GetDeviceFunction(string RoomID, string HomeID)
		{
			var deviceFunctions = _digitalDeviceContext.Rooms
				.Where(r => r.RoomId == RoomID && r.HomeId == HomeID ) // 🔹 Lọc theo RoomID & HomeID / và loại đi room của vehicle
				.Join(_digitalDeviceContext.Devices,
					r => r.RoomId,
					d => d.RoomId,
					(r, d) => new { r, d }) // Join Room với Device
				.Join(_digitalDeviceContext.DeviceFunctions,
					combined => combined.d.DeviceId,
					df => df.DeviceId,
					(combined, df) => new { combined.r, combined.d, df }) // Join với DeviceFunctions
				.Join(_digitalDeviceContext.DeviceFunctionalities,
					combined => combined.df.FunctionId,
					dfy => dfy.FunctionId,
					(combined, dfy) => new
					{
						DeviceID = combined.d.DeviceId,
						NameDevice = combined.d.NameDevice,
						FunctionID = dfy.FunctionId,
						Description = dfy.Description,
						RoomID = combined.r.RoomId,
						HomeID = combined.r.HomeId
					}) // Thêm RoomID & HomeID vào kết quả
				.ToList();

			var devices = new List<object>();

			// 🔹 Thêm thiết bị theo thứ tự ưu tiên
			foreach (var device in deviceFunctions.Where(d => d.FunctionID == "F_Air"))
			{
				devices.Add(device);
			}
			foreach (var device in deviceFunctions.Where(d => d.FunctionID == "F_WaterHeater" || d.FunctionID == "F_Different"))
			{
				devices.Add(device);
			}
			foreach (var device in deviceFunctions.Where(d => d.FunctionID == "F_lamp"))
			{
				devices.Add(device);
			}

			return Ok(devices);
		}

		[HttpGet]
		[Route("GetAllDevice")]
		public List<Device> GetAllDevice(String homeId)
		{
			var query = from home in _digitalDeviceContext.Homes
						join room in _digitalDeviceContext.Rooms
						on home.HomeId equals room.HomeId
						join device in _digitalDeviceContext.Devices
						on room.RoomId equals device.RoomId
						where home.HomeId == homeId
						select new Device
						{
							DeviceId = device.DeviceId,
							NameDevice = device.NameDevice
						};

			return query.ToList();
		}
		


		[HttpGet]
		[Route("GetAllVehicleByHome")]
		public List<Device> GetAllVehicle(string homeId)
		{
			var query = from home in _digitalDeviceContext.Homes
						join room in _digitalDeviceContext.Rooms
						on home.HomeId equals room.HomeId
						join device in _digitalDeviceContext.Devices
						on room.RoomId equals device.RoomId
						where home.HomeId == homeId && room.RoomId.StartsWith("vehicle_")
						select new Device
						{
							DeviceId = device.DeviceId,
							NameDevice = device.NameDevice,
							PhotoPath = device.PhotoPath
						};

			return query.ToList();
		}

	}
}
