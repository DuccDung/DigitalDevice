using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class Authority
{
	public string AuthorityId { get; set; } = null!;
	public string NameAuthority { get; set; } = null!;

	public string? Description { get; set; }

	public virtual ICollection<HomeUser> HomeUsers { get; set; } = new List<HomeUser>(); // Đảm bảo có danh sách này

}
