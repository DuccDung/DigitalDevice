using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class HomeUser
{
    public string HomeUserId { get; set; } = null!;

    public string? UserId { get; set; }

    public string? HomeId { get; set; }

    public string? Description { get; set; }

    public virtual Home? Home { get; set; }

    public virtual User? User { get; set; }
}
