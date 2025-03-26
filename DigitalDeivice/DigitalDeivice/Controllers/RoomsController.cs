using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class RoomsController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public RoomsController(DigitalDeviceContext digitalDeviceContext)
		{
			_digitalDeviceContext = digitalDeviceContext;
		}

		//lấy thêm số thiết bị của phòng đó
		[HttpGet]
		[Route("GetRoomsByHomeID")]
		public IActionResult GetRooms(string HomeID)
		{
			var rooms = _digitalDeviceContext.Rooms
				.Where(x => x.HomeId == HomeID && x.RoomId != "r_000")
				.Select(room => new
				{
					// Lấy tất cả các cột của Room
					room.RoomId,
					room.Name,
					room.HomeId,
					room.PhotoPath,
					// Thêm số lượng thiết bị
					DeviceCount = _digitalDeviceContext.Devices
						.Count(df => df.RoomId == room.RoomId)
				})
				.ToList();

			return Ok(rooms);
		}

		//thêm xóa phòng
		[HttpDelete]
		[Route("DeleteRoom")]
		public IActionResult DeleteRoom(string RoomId)
		{
			// Kiểm tra phòng có tồn tại không
			var room = _digitalDeviceContext.Rooms.Find(RoomId);
			if (room == null)
			{
				return BadRequest("Phòng không tồn tại");
			}

			// Kiểm tra xem phòng có thiết bị nào không
			var hasDevices = _digitalDeviceContext.Devices
				.Any(df => df.RoomId == RoomId);

			if (hasDevices)
			{
				return BadRequest("Không thể xóa phòng đang có thiết bị");
			}

			try
			{
				// Xóa phòng
				_digitalDeviceContext.Rooms.Remove(room);
				_digitalDeviceContext.SaveChanges();
				return Ok("Xóa phòng thành công");
			}
			catch (Exception ex)
			{
				return BadRequest("Lỗi khi xóa phòng: " + ex.Message);
			}
		}

		//thêm add phòng
		//[HttpPost]
		//[Route("AddRoom")]
		//public IActionResult AddRoom(Room room)
		//{

		//}


		





	}
}
