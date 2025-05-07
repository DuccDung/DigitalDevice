using DigitalDeivice.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	[Authorize]
	public class RoomsController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public RoomsController(DigitalDeviceContext digitalDeviceContext) {
			_digitalDeviceContext = digitalDeviceContext;
		}
		//lấy thêm số thiết bị của phòng đó
		[HttpGet]
		[Route("GetRoomsByHomeID")]
		public IActionResult GetRooms(string HomeID)
		{
			var rooms = _digitalDeviceContext.Rooms
				.Where(x => x.HomeId == HomeID && x.RoomId.StartsWith("r_"))
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

		//thêm upload RoomImage
		[HttpPost]
		[Route("UploadRoomImage")]
		public async Task<IActionResult> UploadImageAsync(IFormFile file)
		{
			if (file == null || file.Length == 0)
				return BadRequest("Không có file nào được tải lên");

			// Tạo tên file duy nhất
			string fileName = Guid.NewGuid().ToString() + Path.GetExtension(file.FileName);

			// Đường dẫn đầy đủ đến thư mục lưu trữ
			string uploadsFolder = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", "images");

			// Đảm bảo thư mục tồn tại
			if (!Directory.Exists(uploadsFolder))
				Directory.CreateDirectory(uploadsFolder);

			// Đường dẫn đầy đủ đến file
			string filePath = Path.Combine(uploadsFolder, fileName);

			// Lưu file
			using (var stream = new FileStream(filePath, FileMode.Create))
			{
				await file.CopyToAsync(stream);
			}

			// Trả về đường dẫn tương đối để lưu vào DB
			return Ok(new { path = $"/images/{fileName}" });
		}
		[HttpPost]
		[Route("CreateRoom")]
		public IActionResult CreateRoom(String Name, String HomeId, String PhotoPath)
		{
			var RoomId = "r_00" + _digitalDeviceContext.Rooms.Count().ToString();
			Room room = new Room()
			{
				RoomId = RoomId,
				HomeId = HomeId,
				PhotoPath = PhotoPath,
				Name = Name
			};
			_digitalDeviceContext.Rooms.Add(room);
			_digitalDeviceContext.SaveChanges();
			return Ok(room);

		}

	}
}
