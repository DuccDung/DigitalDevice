using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class DevicesController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public DevicesController(DigitalDeviceContext digitalDeviceContext)
		{
			_digitalDeviceContext = digitalDeviceContext;
		}

		[HttpGet]
		[Route("GetDevicesByRoomID")]
		public IActionResult GetDeviceFunction(string RoomID, string HomeID)
		{
			var deviceFunctions = _digitalDeviceContext.Rooms
				.Where(r => r.RoomId == RoomID && r.HomeId == HomeID) // 🔹 Lọc theo RoomID & HomeID
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


	}
}
